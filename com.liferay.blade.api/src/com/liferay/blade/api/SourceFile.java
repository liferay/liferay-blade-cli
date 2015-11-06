package com.liferay.blade.api;

import java.io.File;

import org.eclipse.core.resources.IFile;

public interface SourceFile {

	IFile getIFile(File file);
	
	void setFile(File file);

}
