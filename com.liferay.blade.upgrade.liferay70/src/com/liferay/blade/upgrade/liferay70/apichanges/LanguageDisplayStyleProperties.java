package com.liferay.blade.upgrade.liferay70.apichanges;

import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.FileMigrator;

@Component(
	property = {
		"file.extensions=properties",
		"problem.title=Language Display Style Properties",
		"problem.summary=Replaced the Language Portlet's Display Styles with ADTs",
		"problem.tickets=LPS-54419",
		"problem.section=#replaced-the-language-portlet",
	},
	service = FileMigrator.class
)
public class LanguageDisplayStyleProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("language.display.style.default");
		properties.add("language.display.style.options");
	}

}
