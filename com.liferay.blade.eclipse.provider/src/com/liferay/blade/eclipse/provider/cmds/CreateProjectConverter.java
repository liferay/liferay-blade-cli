package com.liferay.blade.eclipse.provider.cmds;

import com.liferay.blade.api.ProjectTemplate;

import org.apache.felix.service.command.Converter;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.tracker.ServiceTracker;

@Component
public class CreateProjectConverter implements Converter {

	private ServiceTracker<ProjectTemplate, ProjectTemplate> _templateTracker;

	@Activate
	public void activate(BundleContext context) {
		_templateTracker =
			new ServiceTracker<ProjectTemplate, ProjectTemplate>(
					context, ProjectTemplate.class, null);
		_templateTracker.open();
	}

	@Override
	public Object convert(Class<?> desiredType, Object in) throws Exception {
		if (ProjectTemplate.class.equals(desiredType) && in != null) {
			ProjectTemplate[] projectTemplates = _templateTracker.getServices(new ProjectTemplate[0]);

			for (ProjectTemplate template : projectTemplates) {
				if (template.name().equals(in.toString())) {
					System.out.println("returning template: " + template.name());
					return template;
				}
			}
		}

		return null;
	}

	@Override
	public CharSequence format(Object target, int level, Converter escape) throws Exception {
		return null;
	}

}
