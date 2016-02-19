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

package com.liferay.blade.eclipse.provider;

import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.Reporter;
import com.liferay.blade.eclipse.provider.templates.ReporterTemplateLoader;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@Component(
	property = {
		Constants.SERVICE_RANKING + ":Integer=0",
		"format:String=html"
	}
)
public class HtmlReporter implements Reporter {

	private Configuration _cfg;
	private Map<String, Object> _root;
	private List<Problem> _problems;
	private OutputStream _output;
	private int _detail;

	@Override
	public void beginReporting(int detail, OutputStream output) {
		_detail = detail;
		_output = output;
		_cfg = new Configuration(Configuration.VERSION_2_3_22);
		_cfg.setTemplateLoader(new ReporterTemplateLoader());
		_cfg.setDefaultEncoding("UTF-8");
		_cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

		_root = new HashMap<>();
		_problems = new ArrayList<>();
		_root.put("problems", _problems);
	}

	@Override
	public void endReporting() {
		try {
			Template template = null;
			if(_detail == Migration.DETAIL_SHORT) {
				template = _cfg.getTemplate("html.ftl");
			}
			else {
				template = _cfg.getTemplate("html_detailed.ftl");
			}

			template.process(_root, new OutputStreamWriter(_output));

			_output.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void report(Problem problem) {
		_problems.add(problem);
	}

}
