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
		"problem.title=Replaced ReservedUserIdException with UserIdException Inner Classes",
		"problem.summary=The ReservedUserIdException has been deprecated and replaced with UserIdException.MustNotBeReserved.",
		"problem.tickets=LPS-53487",
		"problem.section=#replaced-reserveduseridexception-with-useridexception-inner-classes"
	},
	service = FileMigrator.class
)
public class ReservedUserIdExceptionCatch  extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findCatchExceptions(new String[]{"ReservedUserIdException"});
	}
}
