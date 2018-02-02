package com.liferay.blade.cli.gradle;

import com.liferay.blade.cli.GogoTelnetClient;
import com.liferay.blade.cli.LiferayBundleDeployer;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;

/**
 * @author Christopher Bryan Boyd
 * @author Gregory Amerson
 */
public class LiferayBundleDeployerImpl implements LiferayBundleDeployer {

	private Optional<GogoTelnetClient> client = Optional.empty();
	private final String host;
	private final int port;

	public LiferayBundleDeployerImpl(final String host, final int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public void close() throws Exception {
		if (client.isPresent()) {
			client.get().close();
			client = Optional.empty();
		}
	}
	private GogoTelnetClient getClient() throws IOException {
		if (!client.isPresent()) {
			client = Optional.of(new GogoTelnetClient(host, port));
		}
		return client.get();
	}

	@Override
	public long getBundleId(Collection<BundleDTO> bundles, String bsn) throws Exception {

		Objects.requireNonNull(bsn);

		return bundles.stream().filter((bundle) -> Objects.equals(bundle.symbolicName, bsn)).map((bundle) -> bundle.id).findAny().orElse(-1L);
	}

	@Override
	public void update(long id, URI uri) throws Exception {
		sendGogo(String.format("update %s %s", id, uri.toASCIIString()));
	}

	@Override
	public void refresh(long id) throws Exception {
		sendGogo(String.format("refresh %s", id));
	}

	@Override
	public void stop(long id) throws Exception {
		sendGogo(String.format("stop %s", id));
	}

	@Override
	public void start(long id) throws Exception {
		sendGogo(String.format("start %s", id));
	}

	@Override
	public long install(URI uri) throws Exception {
		final String installString;

		Path uriPath = Paths.get(uri);

		if (warFileGlob.matches(uriPath)) {
			installString = "install " + getWarString(uriPath);
		}
		else {
			installString = "install " + uri.toASCIIString();
		}

		String response = sendGogo(installString);

		try {
			Matcher matcher = installResponse.matcher(response);

			matcher.matches();

			return Integer.parseInt(matcher.group(1));
		}
		catch (Exception e) {
			throw new Exception("Unable to install bundle.  Unexpected response: \n" + response);
		}
	}

	private static final Pattern installResponse = Pattern.compile(".*Bundle ID: (.*$).*", Pattern.DOTALL | Pattern.MULTILINE);
	private static final PathMatcher warFileGlob = FileSystems.getDefault().getPathMatcher("glob:**.war");
	private static final String warStringTemplate = "webbundle:%s?Web-ContextPath=/%s";

	static String getWarString(Path uri) throws IllegalArgumentException {
		if (!warFileGlob.matches(uri)) {
		    throw new IllegalArgumentException("Must provide a valid WAR file.");
		}

		Path fileName = uri.getFileName();
		String fileNameString = fileName.toString();
		int periodIndex = fileNameString.indexOf('.');

		if (periodIndex > -1) {
			fileNameString = fileNameString.substring(0, periodIndex);
		}

		return String.format(warStringTemplate, uri.toUri().toASCIIString(), fileNameString);
	}

	private String sendGogo(String data) throws Exception{
		final String returnValue;
		try (GogoTelnetClient client = getClient()) {
			returnValue = client.send(data);
		} catch (Exception e) {
			throw e;
		}
		return returnValue;
	}

	private static final String[] parseGogoResponse(String response) {

		return response.split("\\r?\\n");
	}

	private static final String[] parseGogoLine(String line) {

		return line.split("\\|");
	}

	private static List<BundleDTO> _getBundles(GogoTelnetClient client) throws IOException {
		return Stream.of(
				client.send("lb -s -u")
		).map(LiferayBundleDeployerImpl::parseGogoResponse
		).flatMap(Arrays::stream
		).map(LiferayBundleDeployerImpl::getBundleFromGogoLine
		).collect(Collectors.toList());

	}

	private static final BundleDTO getBundleFromGogoLine(String line) {

		try {

			String[] fields = parseGogoLine(line);

			Long id = Long.parseLong(fields[0].trim());

			int state = _getState(fields[1].trim());

			String symbolicName = fields[3];

			return getBundle(id, state, symbolicName);

		}
		catch (Exception e) {

		}
		return null;
	}

	private static final BundleDTO getBundle(Long id, int state, String symbolicName) {
		BundleDTO bundle = new BundleDTO();

		bundle.id = id;
		bundle.state = state;
		bundle.symbolicName = symbolicName;

		return bundle;
	}
	private static final int _getState(String state) {
		String bundleState = state.toUpperCase();

		if ("ACTIVE".equals(bundleState)) {
			return Bundle.ACTIVE;
		}
		else if ("INSTALLED".equals(bundleState)) {
			return Bundle.INSTALLED;
		}
		else if ("RESOLVED".equals(bundleState)) {
			return Bundle.RESOLVED;
		}
		else if ("STARTING".equals(bundleState)) {
			return Bundle.STARTING;
		}
		else if ("STOPPING".equals(bundleState)) {
			return Bundle.STOPPING;
		}
		else if ("UNINSTALLED".equals(bundleState)) {
			return Bundle.UNINSTALLED;
		}

		return 0;
	}

	@Override
	public Collection<BundleDTO> getBundles() throws Exception {
		final Collection<BundleDTO> returnValue;
		try (GogoTelnetClient client = getClient()) {
			returnValue = _getBundles(client);
		} catch (Exception e) {
			throw e;
		}
		return returnValue;
	}
}