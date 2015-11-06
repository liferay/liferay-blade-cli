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
		"problem.title=Created a New getType Method That is Implemented in DLProcessor",
		"problem.summary=The DLProcessor interface has a new method getType().",
		"problem.tickets=LPS-53574",
		"problem.section=#created-a-new-gettype-method-that-is-implemented-in-dlprocessor"
	},
	service = FileMigrator.class
)
public class DLProcessorGetType  extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return  javaFileChecker.findImplementsInterface("DLProcessor");
	}
}
