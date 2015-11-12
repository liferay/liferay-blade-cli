package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
@Component(
	property = {
		"file.extensions=properties",
		"problem.title=Portal Property Changes",
		"problem.summary=Removed Portal Properties Used to Display Sections in Form Navigators",
		"problem.tickets=LPS-54903",
		"problem.section=#removed-portal-properties-used-to-display-sections-in-form-navigators",
	},
	service = FileMigrator.class
)
public class PortalProperties extends PropertiesFileMigrator {

	@Override
	protected void addPropertiesToSearch(List<String> properties) {
		properties.add("company.settings.form.configuration");
		properties.add("company.settings.form.identification");
		properties.add("company.settings.form.miscellaneous");
		properties.add("company.settings.form.social");
		properties.add("layout.form.add");
		properties.add("layout.form.update");
		properties.add("layout.set.form.update");
		properties.add("organizations.form.add.identification");
		properties.add("organizations.form.add.main");
		properties.add("organizations.form.add.miscellaneous");
		properties.add("organizations.form.update.identification");
		properties.add("organizations.form.update.main");
		properties.add("organizations.form.update.miscellaneous");
		properties.add("sites.form.add.advanced");
		properties.add("sites.form.add.main");
		properties.add("sites.form.add.miscellaneous");
		properties.add("sites.form.add.seo");
		properties.add("sites.form.update.advanced");
		properties.add("sites.form.update.main");
		properties.add("sites.form.update.miscellaneous");
		properties.add("sites.form.update.seo");
		properties.add("users.form.add.identification");
		properties.add("users.form.add.main");
		properties.add("users.form.add.miscellaneous");
		properties.add("users.form.my.account.identification");
		properties.add("users.form.my.account.main");
		properties.add("users.form.my.account.miscellaneous");
		properties.add("users.form.update.identification");
		properties.add("users.form.update.main");
		properties.add("users.form.update.miscellaneous");
	}

}