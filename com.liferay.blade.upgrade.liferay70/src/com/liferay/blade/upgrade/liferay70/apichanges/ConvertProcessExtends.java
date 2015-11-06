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
		"problem.title=Convert Process Classes don't support convert.processes Portal Property",
		"problem.summary=The implementation class com.liferay.portal.convert.ConvertProcess was renamed com.liferay.portal.convert.BaseConvertProcess. "
                + "An interface named com.liferay.portal.convert.ConvertProcess was created for it."
                + " The convert.processes key was removed from portal.properties. "
                + "Consequentially, ConvertProcess implementations must register as OSGi components.",
		"problem.tickets=LPS-50604",
		"problem.section=#convert-process-classes-are-no-longer-specified-via-the-convert"
	},
	service = FileMigrator.class
)
public class ConvertProcessExtends extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return  javaFileChecker.findSuperClass("ConvertProcess");
	}
}
