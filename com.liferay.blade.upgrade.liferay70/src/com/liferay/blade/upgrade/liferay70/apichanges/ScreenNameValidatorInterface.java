package com.liferay.blade.upgrade.liferay70.apichanges;

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

@Component(
	property = {
		"file.extensions=java",
		"problem.title=Added New Methods in the ScreenNameValidator Interface",
		"problem.summary=The ScreenNameValidator interface has new methods getDescription(Locale) and getJSValidation().",
		"problem.tickets=LPS-53409",
		"problem.section=#added-new-methods-in-the-screennamevalidator-interface"
	},
	service = FileMigrator.class
)
public class ScreenNameValidatorInterface extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findImplementsInterface("ScreenNameValidator");
	}
}
