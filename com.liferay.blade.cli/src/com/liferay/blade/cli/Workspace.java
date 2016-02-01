package com.liferay.blade.cli;

import java.util.regex.Pattern;

/**
 * @author David Truong
 */
public class Workspace {

	public static final String DEFAULT_BUNDLE_ARTIFACT_NAME =
		"portal-tomcat-bundle";

	public static final String DEFAULT_BUNDLE_ARTIFACT_NAME_PROPERTY =
		"liferay.workspace.bundle.artifact.name";

	public static final String DEFAULT_LIFERAY_HOME_DIR = "bundles";

	public static final String DEFAULT_LIFERAY_HOME_DIR_PROPERTY =
		"liferay.workspace.home.dir";

	public static final String DEFAULT_MODULES_DIR = "modules";

	public static final String DEFAULT_MODULES_DIR_PROPERTY =
		"liferay.workspace.modules.dir";

	public static final String DEFAULT_PLUGINS_SDK_DIR = "plugins-sdk";

	public static final String DEFAULT_PLUGINS_SDK_DIR_PROPERTY =
		"liferay.workspace.plugins.sdk.dir";

	public static final String DEFAULT_THEMES_DIR = "themes";

	public static final String DEFAULT_THEMES_DIR_PROPERTY =
		"liferay.workspace.themes.dir";

	public static final Pattern PATTERN_WORKSPACE_PLUGIN = Pattern.compile(
		".*apply\\s*plugin\\s*:\\s*[\'\"]com\\.liferay\\.workspace[\'\"]\\s*$",
		Pattern.MULTILINE | Pattern.DOTALL );

}