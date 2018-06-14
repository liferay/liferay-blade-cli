package com.liferay.blade.cli.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.util.Properties;

public class WorkspaceMetadata {

	public WorkspaceMetadata(File workspaceMetadataFile) {
		this.workspaceMetadataFile = workspaceMetadataFile;
		load();
	}

	public String getProfileName() {
		return properties.getProperty("blade.profile.name");
	}

	public void load() {
		try {
			properties.load(new FileInputStream(workspaceMetadataFile));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void save() {
		try {
			properties.store(new FileOutputStream(workspaceMetadataFile), null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setProfileName(String profileName) {
		properties.setProperty("blade.profile.name", profileName);
	}

	private Properties properties = new Properties();
	private final File workspaceMetadataFile;

}