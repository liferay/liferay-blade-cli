package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
@Component(
	property = {
		"file.extensions=properties",
		"problem.title=Breadcrumb Portlet's Display Styles Changes",
		"problem.summary=Replaced the Breadcrumb Portlet's Display Styles with ADTs",
		"problem.tickets=LPS-53577",
		"problem.section=#replaced-the-breadcrumb-portlet",
	},
	service = FileMigrator.class
)
public class BreadcrumbProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("breadcrumb.display.style.default");
		properties.add("breadcrumb.display.style.options");
	}

}