package com.liferay.blade.eclipse.provider.cmds;

import com.liferay.blade.api.ProjectTemplate;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"name=servicewrapper"
	}
)
public class ServiceWrapperTemplate implements ProjectTemplate {

	@Override
	public String name() {
		return "servicewrapper";
	}
}