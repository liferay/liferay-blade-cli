package com.liferay.blade.upgrade.liferay70.apichanges;

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.liferay70.ImportStatementMigrator;

@Component(
	property = {
		"file.extensions=java",
		"problem.summary=The classes from package com.liferay.util.bridges.mvc in util-bridges.jar were moved to a new package com.liferay.portal.kernel.portlet.bridges.mvc in portal-service.jar.",
		"problem.tickets=LPS-50156",
		"problem.title=Moved MVCPortlet, ActionCommand and ActionCommandCache from util-bridges.jar to portal-service.jar",
		"problem.section=#moved-mvcportlet-actioncommand-and-actioncommandcache-from-util",
		"auto.correct=import"
	},
	service = {
		FileMigrator.class,
		AutoMigrator.class
	}
)
public class MVCPortletActionCommandImports extends ImportStatementMigrator {

	private final static String[] IMPORTS = new String[] {
			"com.liferay.util.bridges.mvc.ActionCommand",
			"com.liferay.util.bridges.mvc.BaseActionCommand",
			"com.liferay.util.bridges.mvc.MVCPortlet"
	};

	private final static String[] IMPORTS_FIXED = new String[] {
			"com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand",
			"com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand",
			"com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet"
	};

	public MVCPortletActionCommandImports() {
		super(IMPORTS, IMPORTS_FIXED);
	}

}