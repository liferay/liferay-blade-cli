/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.cli;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.dto.BundleDTO;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author Christopher Bryan Boyd
 */
public interface LiferayBundleDeployer extends AutoCloseable {
	static LiferayBundleDeployer _getDefault(String host, int port) {
		return new LiferayBundleDeployerImpl(host, port);
	}
	static LiferayBundleDeployer _getDefault(InetSocketAddress address) {
		return new LiferayBundleDeployerImpl(address);
	}
	
	Collection<BundleDTO> getBundles() throws Exception;

	default long getBundleId(String name) throws Exception {
		return getBundleId(getBundles(), name);
	}
	
	long getBundleId(Collection<BundleDTO> bundles, String name) throws Exception;
	
	void update(long id, URI uri) throws Exception;
	
	void refresh(long id) throws Exception;
	
	void stop(long id) throws Exception;
	
	void start(long id) throws Exception;
	
	long install(URI uri) throws Exception;
	
	default void reloadFragment(long id, long hostId, URI uri) throws Exception {
		
		update(id, uri);
		
		refresh(hostId);
		
	}
	
	default void reloadBundle(long id, URI uri) throws Exception {
		
		stop(id);
		
		update(id, uri);
		
		start(id);
		
	}
	
	public static String _getWarString(Path uri) throws IllegalArgumentException {
		if (!Constants.matcher.matches(uri)) {
		    throw new IllegalArgumentException("Must provide a valid WAR file.");
		}
		Path fileName = uri.getFileName();
		String fileNameString = fileName.toString();
		int periodIndex = fileNameString.indexOf('.');
		if (periodIndex > -1) {
			fileNameString = fileNameString.substring(0, periodIndex);
		}
		return String.format(Constants.warStringTemplate, uri.toUri().toASCIIString(), fileNameString);
	}
}

class LiferayBundleDeployerImpl implements LiferayBundleDeployer {

	private Optional<GogoTelnetClient> client = Optional.empty();
	private final String host;
	private final int port;
	
	public LiferayBundleDeployerImpl(final InetSocketAddress address) {
		this(address.getHostString(), address.getPort());
	}
	
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
		
		if (Constants.matcher.matches(uriPath)) {
			
			installString = "install " + LiferayBundleDeployer._getWarString(uriPath);
			
		} else {
			
			installString = "install " + uri.toASCIIString();
			
		}
		
		String response = sendGogo(installString);
		
		InstallResponse installResponse = InstallResponse.getResponse(response);
		
		return installResponse.getBundleId();
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

@Parameters(commandNames="Bundle")
class InstallResponse {
	static InstallResponse getResponse(String data) throws IllegalArgumentException {
		JCommander commander = JCommander.newBuilder().addCommand(new InstallResponse()).build();
		
		if (Objects.nonNull(data) && data.startsWith("Bundle") && data.indexOf(' ') > -1) {
			
			String[] args = data.split(" ");
			
			commander.parse(args);
			
			String command = commander.getParsedCommand();

			Map<String, JCommander> commands = commander.getCommands();

			JCommander jcommander = commands.get(command);

			List<Object> objects = jcommander.getObjects();

			Object commandArgs = objects.get(0);

			Objects.requireNonNull(commandArgs);
			
			InstallResponse response = (InstallResponse)commandArgs;
			
			return response;
		}
		else {
			throw new IllegalArgumentException("data \"" + data + "\" is invalid.");
		}
	}
	
	@Parameter(names = "ID:")
	private Integer bundleId = -1;

	public Integer getBundleId() {
		return bundleId;
	}
	
	
}

class Constants {	
	static final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.war");
	static final String warStringTemplate = "webbundle:%s?Web-ContextPath=/%s";
	
}