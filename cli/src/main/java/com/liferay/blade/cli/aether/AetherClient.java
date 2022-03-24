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

package com.liferay.blade.cli.aether;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilder;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;

import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.version.Version;

/**
 * @author Gregory Amerson
 */
public class AetherClient {

	public AetherClient() {
		this(_DEFAULT_REPO_URLS);
	}

	public AetherClient(String[] repoUrls) {
		this(repoUrls, _lookupLocalRepoDir().getPath());
	}

	public AetherClient(String[] repoUrls, String localRepositoryPath) {
		_repoUrls = (repoUrls == null) ? new String[0] : repoUrls;
		_localRepositoryPath = localRepositoryPath;
	}

	public Artifact findLatestAvailableArtifact(String groupIdArtifactId) throws ArtifactResolutionException {
		final RepositorySystem system = _newRepositorySystem();
		final List<RemoteRepository> repos = _repos();

		final String range = "[0,)";

		final Artifact artifactRange = new DefaultArtifact(groupIdArtifactId + ":" + range);

		final VersionRangeRequest rangeRequest = new VersionRangeRequest();

		rangeRequest.setArtifact(artifactRange);
		rangeRequest.setRepositories(repos);

		final RepositorySystemSession session = _newRepositorySystemSession(system, _localRepositoryPath);

		Version version = null;

		try {
			VersionRangeResult versionRangeResult = system.resolveVersionRange(session, rangeRequest);

			version = versionRangeResult.getHighestVersion();
		}
		catch (Exception exception) {
		}

		if (version == null) {
			return null;
		}

		Artifact artifact = new DefaultArtifact(groupIdArtifactId + ":" + version);
		ArtifactRequest artifactRequest = new ArtifactRequest();

		artifactRequest.setArtifact(artifact);
		artifactRequest.setRepositories(repos);

		ArtifactResult artifactResult = system.resolveArtifact(session, artifactRequest);

		return artifactResult.getArtifact();
	}

	private static Settings _buildSettings() {
		SettingsBuildingRequest request = new DefaultSettingsBuildingRequest();

		request.setGlobalSettingsFile(_DEFAULT_GLOBAL_SETTINGS_FILE);
		request.setUserSettingsFile(_USER_MAVEN_DEFAULT_USER_SETTINGS_FILE);

		try {
			DefaultSettingsBuilderFactory defaultSettingsBuilderFactory = new DefaultSettingsBuilderFactory();

			DefaultSettingsBuilder builder = defaultSettingsBuilderFactory.newInstance();

			return builder.build(
				request
			).getEffectiveSettings();
		}
		catch (SettingsBuildingException settingsBuildingException) {
			return null;
		}
	}

	private static File _lookupLocalRepoDir() {
		String localRepoPathSetting = _buildSettings().getLocalRepository();

		if (localRepoPathSetting == null) {
			return new File(_USER_MAVEN_CONFIGURATION_HOME, "repository");
		}

		return new File(localRepoPathSetting);
	}

	private RemoteRepository _newRemoteRepository(String url) {
		return new RemoteRepository.Builder(
			"blade", "default", url
		).build();
	}

	private RepositorySystem _newRepositorySystem() {
		DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();

		locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
		locator.addService(TransporterFactory.class, FileTransporterFactory.class);
		locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

		DefaultServiceLocator.ErrorHandler handler = new DefaultServiceLocator.ErrorHandler() {

			@Override
			public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
				exception.printStackTrace();
			}

		};

		locator.setErrorHandler(handler);

		return locator.getService(RepositorySystem.class);
	}

	private DefaultRepositorySystemSession _newRepositorySystemSession(
		RepositorySystem system, String localRepositoryPath) {

		final DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

		final LocalRepository localRepo = new LocalRepository(localRepositoryPath);

		session.setUpdatePolicy(RepositoryPolicy.UPDATE_POLICY_DAILY);
		session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
		session.setTransferListener(new NoopTransferListener());
		session.setRepositoryListener(new NoopRepositoryListener());

		return session;
	}

	private List<RemoteRepository> _repos() {
		final List<RemoteRepository> repos = new ArrayList<>();

		for (String repoUrl : _repoUrls) {
			repos.add(_newRemoteRepository(repoUrl));
		}

		return Collections.unmodifiableList(repos);
	}

	private static final File _DEFAULT_GLOBAL_SETTINGS_FILE = new File(
		System.getProperty("maven.home", System.getProperty("user.dir", "")), "conf/settings.xml");

	private static final String[] _DEFAULT_REPO_URLS = {
		"https://repository-cdn.liferay.com/nexus/content/groups/public"
	};

	private static final String _USER_HOME = System.getProperty("user.home");

	private static final File _USER_MAVEN_CONFIGURATION_HOME = new File(_USER_HOME, ".m2");

	private static final File _USER_MAVEN_DEFAULT_USER_SETTINGS_FILE = new File(
		_USER_MAVEN_CONFIGURATION_HOME, "settings.xml");

	private final String _localRepositoryPath;
	private final String[] _repoUrls;

	private static class NoopRepositoryListener extends AbstractRepositoryListener {
	}

	private static class NoopTransferListener extends AbstractTransferListener {
	}

}