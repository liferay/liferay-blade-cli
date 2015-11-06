package com.liferay.blade.upgrade.liferay70;

import com.liferay.blade.api.JavaFile;

public abstract class JavaFileMigrator extends AbstractFileMigrator<JavaFile> {

	public JavaFileMigrator() {
		super(JavaFile.class);
	}

}
