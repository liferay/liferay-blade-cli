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
		"problem.title=Replaced the ReservedUserEmailAddressException with UserEmailAddressException",
		"problem.section=#replaced-the-reserveduseremailaddressexception-with-useremailaddressexception-inner-classes-in-user-services",
		"problem.summary=Replaced the ReservedUserEmailAddressException with UserEmailAddressException Inner Classes in User Services",
		"problem.tickets=LPS-53279",
	},
	service = FileMigrator.class
)
public class ReplacedReservedUserEmailAddressException extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findCatchExceptions(new String[] {"ReservedUserEmailAddressException"});
	}
}