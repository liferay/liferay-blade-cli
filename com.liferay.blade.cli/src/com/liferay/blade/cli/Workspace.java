package com.liferay.blade.cli;

import java.util.regex.Pattern;

/**
 * @author David Truong
 */
public class Workspace {

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