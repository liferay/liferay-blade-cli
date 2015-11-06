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
		"problem.title=Changed the Usage of Asset Preview",
		"problem.section=#changed-the-usage-of-asset-preview",
		"problem.summary=Changed the Usage of Asset Preview",
		"problem.tickets=LPS-53972",
	},
	service = FileMigrator.class
)
public class AssetPreviewTags extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {

		return javaFileChecker.findMethodInvocations("AssetRenderer", null,
				"getPreviewPath", new String[] { "PortletRequest","PortletResponse" });
	}

}