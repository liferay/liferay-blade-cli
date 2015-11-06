package com.liferay.blade.eclipse.provider.templates;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import freemarker.cache.TemplateLoader;

public class ReporterTemplateLoader implements TemplateLoader {

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		if (templateSource instanceof ReporterTemplate) {
			ReporterTemplate template = (ReporterTemplate) templateSource;
			template.getReader().close();
		}
	}

	@Override
	public Object findTemplateSource(String name) throws IOException {
		ReporterTemplate retval = null;

		InputStream resource = this.getClass().getResourceAsStream(name);

		if( resource != null ) {
			// found template
			retval = new ReporterTemplate(resource);
		}

		return retval;
	}

	@Override
	public long getLastModified(Object arg0) {
		return -1;
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		Reader retval = null;

		if (templateSource instanceof ReporterTemplate) {
			ReporterTemplate template = (ReporterTemplate) templateSource;

			retval = template.getReader();
		}

		return retval;
	}

}
