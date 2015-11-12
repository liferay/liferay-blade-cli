package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=java",
		"problem.summary=Removed render Method from ConfigurationAction API",
		"problem.tickets=LPS-56300",
		"problem.title=ConfigurationAction render method",
		"problem.section=#removed-render-method-from-configurationaction-api"
	},
	service = FileMigrator.class
)
public class ConfigurationActionRenderMethod extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		final List<SearchResult> searchResults = new ArrayList<>();

		// render method declarations
		List<SearchResult> declarations = javaFileChecker.findMethodDeclaration("render",
				new String[] { "PortletConfig", "RenderRequest", "RenderResponse" });

		searchResults.addAll(declarations);

		// render method invocations
		List<SearchResult> invocations = javaFileChecker.findMethodInvocations(
				"ConfigurationAction", null, "render", null);

		searchResults.addAll(invocations);

		return searchResults;
	}
}