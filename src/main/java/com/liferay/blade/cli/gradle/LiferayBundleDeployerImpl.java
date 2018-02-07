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

	public LiferayBundleDeployerImpl(final String host, final int port) throws IOException {
		_client = new GogoTelnetClient(host, port);
	}

	@Override
	public void close() throws Exception {
		if (_client != null) {
			_client.close();
		}
	}

	@Override
	public long getBundleId(Collection<BundleDTO> bundles, String bsn) throws Exception {
		long bundleId;

		if (Objects.nonNull(bsn)) {
			bundleId = bundles.stream().filter(
				Objects::nonNull
			).filter((bundle) ->
				Objects.equals(bundle.symbolicName, bsn)
			).map((bundle) ->
				bundle.id
			).findAny(
			).orElse(
				-1L
			);

		} else {
			bundleId = -1L;
		}

		return bundleId;
	}

	@Override
	public Collection<BundleDTO> getBundles() throws Exception {
		return _getBundles(_client);
	}

	@Override
	public long install(URI uri) throws Exception {
		final String installString;

		Path uriPath = Paths.get(uri);

		if (_warFileGlob.matches(uriPath)) {
			installString = "install " + _getWarString(uriPath);
		}
		else {
			installString = "install " + uri.toASCIIString();
		}

		String response = _sendGogo(installString);

		try {
			Matcher matcher = _installResponse.matcher(response);

			matcher.matches();

			return Integer.parseInt(matcher.group(1));
		}
		catch (Exception e) {
			throw new Exception("Unable to install bundle.  Unexpected response: \n" + response);
		}
	}

	@Override
	public void refresh(long id) throws Exception {
		_sendGogo(String.format("refresh %s", id));
	}

	@Override
	public void start(long id) throws Exception {
		_sendGogo(String.format("start %s", id));
	}

	@Override
	public void stop(long id) throws Exception {
		_sendGogo(String.format("stop %s", id));
	}

	@Override
	public void update(long id, URI uri) throws Exception {
		_sendGogo(String.format("update %s %s", id, uri.toASCIIString()));
	}

	private static List<BundleDTO> _getBundles(GogoTelnetClient client) throws IOException {
		String response = client.send("lb -s -u");

		return Stream.of(
			response
		).map(
			LiferayBundleDeployerImpl::_parseGogoResponse
		).flatMap(
			Arrays::stream
		).map(
			LiferayBundleDeployerImpl::parseGogoLine
		).collect(
			Collectors.toList()
		);
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

	private static String _getWarString(Path uri) throws IllegalArgumentException {
		if (!_warFileGlob.matches(uri)) {
			throw new IllegalArgumentException("Must provide a valid WAR file.");
		}

		Path fileName = uri.getFileName();

		String fileNameString = fileName.toString();

		int periodIndex = fileNameString.indexOf('.');

		if (periodIndex > -1) {
			fileNameString = fileNameString.substring(0, periodIndex);
		}

		return String.format(_warStringTemplate, uri.toUri().toASCIIString(), fileNameString);
	}

	private static final String[] _parseGogoResponse(String response) {
		return response.split("\\r?\\n");
	}

	private static final BundleDTO newBundleDTO(Long id, int state, String symbolicName) {
		BundleDTO bundle = new BundleDTO();

		bundle.id = id;
		bundle.state = state;
		bundle.symbolicName = symbolicName;

		return bundle;
	}

	private static final BundleDTO parseGogoLine(String line) {
		String[] fields = line.split("\\|");

		Long id = Long.parseLong(fields[0].trim());

		int state = _getState(fields[1].trim());

		String symbolicName = fields[3];

		return newBundleDTO(id, state, symbolicName);
	}

	private String _sendGogo(String data) throws Exception {
		return _client.send(data);
	}

	private static final Pattern _installResponse = Pattern.compile(
		".*Bundle ID: (.*$).*", Pattern.DOTALL | Pattern.MULTILINE);
	private static final PathMatcher _warFileGlob = FileSystems.getDefault().getPathMatcher("glob:**.war");
	private static final String _warStringTemplate = "webbundle:%s?Web-ContextPath=/%s";

	private GogoTelnetClient _client;

}