package com.liferay.blade.eclipse.provider.templates;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ReporterTemplate {

	private InputStream _resource;

	public ReporterTemplate(InputStream resource) {
		_resource = resource;
	}

	public Reader getReader() {
		return new BufferedReader(new InputStreamReader(_resource));
	}

}
