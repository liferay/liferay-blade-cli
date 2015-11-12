package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=java",
		"problem.title=Changes in Exceptions Thrown by User Services",
		"problem.section=#changes-in-exceptions-thrown-by-user-services",
		"problem.summary=Changes in Exceptions Thrown by User Services",
		"problem.tickets=LPS-47130",
	},
	service = FileMigrator.class
)
public class UserServicesThrownExceptionsChanges extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findCatchExceptions(new String[] {"DuplicateUserScreenNameException", "DuplicateUserEmailAddressException"});
	}
}