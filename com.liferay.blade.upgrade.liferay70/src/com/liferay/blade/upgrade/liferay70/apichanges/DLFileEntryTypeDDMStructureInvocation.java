package com.liferay.blade.upgrade.liferay70.apichanges;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.title=DLFileEntryTypeLocalServiceUtil Api Changes",
		"problem.section=#removed-the-dlfileentrytypes",
		"problem.summary=Removed the DLFileEntryTypes_DDMStructures Mapping Table",
		"problem.tickets=LPS-56660",
	},
	service = FileMigrator.class
)
public class DLFileEntryTypeDDMStructureInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(
		File file, JavaFile javaFileChecker) {

		final List<SearchResult> result = new ArrayList<SearchResult>();

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil",
				"addDDMStructureDLFileEntryType", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil",
				"addDDMStructureDLFileEntryTypes", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil",
				"clearDDMStructureDLFileEntryTypes", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil",
				"deleteDDMStructureDLFileEntryType", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil",
				"deleteDDMStructureDLFileEntryTypes", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil",
				"getDDMStructureDLFileEntryTypes", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil",
				"getDDMStructureDLFileEntryTypesCount", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil",
				"hasDDMStructureDLFileEntryType", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil",
				"hasDDMStructureDLFileEntryTypes", null));

		result.addAll(
			javaFileChecker.findMethodInvocations(
				null, "DLFileEntryTypeLocalServiceUtil",
				"setDDMStructureDLFileEntryTypes", null));

		return result;
	}

}
