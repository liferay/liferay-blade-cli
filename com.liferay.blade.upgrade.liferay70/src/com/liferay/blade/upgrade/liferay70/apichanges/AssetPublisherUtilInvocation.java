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
		"file.extensions=java,jsp,jspf",
		"problem.title=Moved the AssetPublisherUtil Class and Removed It from the Public API",
		"problem.section=#moved-the-assetpublisherutil-class-and-removed-it-from-the-public-api",
		"problem.summary=Moved the AssetPublisherUtil Class and Removed It from the Public API",
		"problem.tickets=LPS-52744",
	},
	service = FileMigrator.class
)
public class AssetPublisherUtilInvocation extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findMethodInvocations(null, "AssetPublisherUtil", "*", null);
	}
}
