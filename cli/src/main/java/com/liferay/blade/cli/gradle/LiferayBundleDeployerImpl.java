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

package com.liferay.blade.cli.gradle;

import com.liferay.blade.cli.LiferayBundleDeployer;
import com.liferay.gogo.shell.client.GogoShellClient;

import java.io.IOException;

import java.net.URI;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

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
		_client = new GogoShellClient(host, port);
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
			Stream<BundleDTO> stream = bundles.stream();

			bundleId = stream.filter(
				Objects::nonNull
			).filter(
				bundle -> Objects.equals(bundle.symbolicName, bsn)
			).map(
				bundle -> bundle.id
			).findAny(
			).orElse(
				-1L
			);
		}
		else {
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

		if (_WAR_FILE_GLOB.matches(uriPath)) {
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
			throw new Exception("Unable to install bundle.  Unexpected response: \n" + response, e);
		}
	}

	@Override
	public void refresh(long id) throws Exception {
		String output = _sendGogo(String.format("refresh %s", id));

		System.out.println(output);
	}

	@Override
	public void start(long id) throws Exception {
		String request = String.format("start %s", id);

		String response = _sendGogo(request);

		_verify(request, response);
	}

	@Override
	public void stop(long id) throws Exception {
		String output = _sendGogo(String.format("stop %s", id));

		System.out.println(output);
	}

	@Override
	public void update(long id, URI uri) throws Exception {
		String output = _sendGogo(String.format("update %s %s", id, uri.toASCIIString()));

		System.out.println(output);
	}

	private static List<BundleDTO> _getBundles(GogoShellClient client) throws IOException {
		String response = client.send("lb -s -u");

		String[] lines = _parseGogoResponse(response);

		return Stream.of(
			lines
		).skip(
			3
		).map(
			LiferayBundleDeployerImpl::_parseGogoLine
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

	private static String _getWarString(Path path) throws IllegalArgumentException {
		if (!_WAR_FILE_GLOB.matches(path)) {
			throw new IllegalArgumentException("Must provide a valid WAR file");
		}

		Path fileName = path.getFileName();

		String fileNameString = fileName.toString();

		int periodIndex = fileNameString.indexOf('.');

		if (periodIndex > -1) {
			fileNameString = fileNameString.substring(0, periodIndex);
		}

		URI uri = path.toUri();

		return String.format(_WAR_STRING_TEMPLATE, uri.toASCIIString(), fileNameString);
	}

	private static final BundleDTO _newBundleDTO(Long id, int state, String symbolicName) {
		BundleDTO bundle = new BundleDTO();

		bundle.id = id;
		bundle.state = state;
		bundle.symbolicName = symbolicName;

		return bundle;
	}

	private static final BundleDTO _parseGogoLine(String line) {
		String[] fields = line.split("\\|");

		Long id = Long.parseLong(fields[0].trim());

		int state = _getState(fields[1].trim());

		String symbolicName = fields[3];

		return _newBundleDTO(id, state, symbolicName);
	}

	private static final String[] _parseGogoResponse(String response) {
		return response.split("\\r?\\n");
	}

	private static void _verify(String request, String response) throws Exception {
		Objects.requireNonNull(request, "Request cannot be null");
		Objects.requireNonNull(request, "Response cannot be null");
		request = request.trim();
		response = response.trim();

		String requestWithoutBreaks = request.replace(System.lineSeparator(), "");
		String responseWithoutBreaks = response.replace(System.lineSeparator(), "");
		int requestLineBreakCount = request.length() - requestWithoutBreaks.length();
		int responseLineBreakCount = response.length() - responseWithoutBreaks.length();

		if (requestLineBreakCount != responseLineBreakCount) {
			String exceptionString =
				"Unexpected exception encountered while processing command \"" + request + "\":" +
					System.lineSeparator() + response;

			throw new Exception(exceptionString);
		}
		else {
			String[] requestSplit = request.split(" ");
			String[] responseSplit = response.split(" ");

			if (requestSplit.length != responseSplit.length) {
				String exceptionString =
					"Unexpected response encountered while processing command \"" + request + "\":" +
						System.lineSeparator() + response;

				throw new Exception(exceptionString);
			}
			else {
				for (int x = 0; x < requestSplit.length; x++) {
					if (!Objects.equals(requestSplit[0], responseSplit[0])) {
						String exceptionString =
							"Unexpected response encountered while processing command \"" + request + "\":" +
								System.lineSeparator() + response;

						throw new Exception(exceptionString);
					}
				}
			}
		}
	}

	private String _sendGogo(String data) throws Exception {
		return _client.send(data);
	}

	private static final FileSystem _FILE_SYSTEM = FileSystems.getDefault();

	private static final PathMatcher _WAR_FILE_GLOB = _FILE_SYSTEM.getPathMatcher("glob:**.war");

	private static final String _WAR_STRING_TEMPLATE = "webbundle:%s?Web-ContextPath=/%s";

	private static final Pattern _installResponse = Pattern.compile(
		".*Bundle ID: (.*$).*", Pattern.DOTALL | Pattern.MULTILINE);

	private GogoShellClient _client;

}