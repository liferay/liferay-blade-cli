package com.liferay.blade.eclipse.provider.cmds;

import com.liferay.blade.api.ProjectTemplate;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"name=service"
	}
)
public class ServiceTemplate implements ProjectTemplate {

	@Override
	public String name() {
		return "service";
	}
}