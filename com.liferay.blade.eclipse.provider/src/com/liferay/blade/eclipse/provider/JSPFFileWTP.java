package com.liferay.blade.eclipse.provider;

import com.liferay.blade.api.JSPFile;
import com.liferay.blade.api.JavaFile;

import java.io.File;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extension=jspf"
	},
	service = {
		JavaFile.class,
		JSPFile.class
	}
)
public class JSPFFileWTP extends JSPFileWTP {

	public JSPFFileWTP() {
	}

	public JSPFFileWTP(File file) {
		super(file);
	}

}