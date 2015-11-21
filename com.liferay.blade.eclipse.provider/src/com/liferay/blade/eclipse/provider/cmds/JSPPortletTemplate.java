package com.liferay.blade.eclipse.provider.cmds;

import com.liferay.blade.api.ProjectTemplate;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"name=jspportlet"
	}
)
public class JSPPortletTemplate implements ProjectTemplate {

	@Override
	public String name() {
		return "jspportlet";
	}
}