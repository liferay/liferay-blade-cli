package com.liferay.blade.upgrade.liferay70.deprecatedmethods;

import com.liferay.blade.api.FileMigrator;

import org.osgi.service.component.annotations.Component;

@Component(
		property = {
			"file.extensions=java,jsp,jspf",
			"implName=DeprecatedVersion61MethodsInvocation"
		},
		service = FileMigrator.class
	)
public class DeprecatedVersion61MethodsInvocation extends AbstractDeprecatedMethodsInvocation {

	@Override
	protected String getJsonFilePath() {

		return "/com/liferay/blade/upgrade/liferay70/deprecatedmethods/deprecatedMethods61.json";
	}

}
