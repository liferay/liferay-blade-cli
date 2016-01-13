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
		"file.extensions=java,jsp,jspf",
		"problem.summary=The getURLView method of AssetRenderer returns String instead of PortletURL",
		"problem.tickets=LPS-61853",
		"problem.title=AssetRenderer API Changes",
		"problem.section=#the-geturlview-method-of-assetrenderer-returns-string-instead-of-portleturl"
	},
	service = FileMigrator.class
)
public class AssetRendererGetURLViewDecl extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findMethodDeclaration("getURLView",
				new String[] { "LiferayPortletResponse", "WindowState" });
	}
}