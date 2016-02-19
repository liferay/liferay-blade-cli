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
