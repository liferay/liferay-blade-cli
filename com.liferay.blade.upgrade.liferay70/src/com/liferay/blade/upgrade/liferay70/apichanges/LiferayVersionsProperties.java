package com.liferay.blade.upgrade.liferay70.apichanges;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.osgi.service.component.annotations.Component;

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.PropertiesFileChecker;
import com.liferay.blade.upgrade.liferay70.PropertiesFileChecker.KeyInfo;

import aQute.lib.io.IO;

@Component(
	property = {
		"file.extensions=properties",
		"problem.title=liferay-versions key in Liferay Plugin Packages Properties",
		"problem.summary=In order to deploy this project to 7.0 the liferay-versions property must be set to 7.0.0+",
		"problem.tickets=",
		"problem.section=",
		"auto.correct=property"
	},
	service = {
		FileMigrator.class,
		AutoMigrator.class
	}
)
public class LiferayVersionsProperties extends PropertiesFileMigrator implements AutoMigrator {

	private static final String PREFIX = "property:";

	@Override
	protected void addPropertiesToSearch(List<String> _properties) {
	}

	@Override
	public List<Problem> analyze(File file) {
		final List<Problem> problems = new ArrayList<>();

		if (file.getName().equals("liferay-plugin-package.properties")) {
			PropertiesFileChecker propertiesFileChecker =
					new PropertiesFileChecker(file);

			List<KeyInfo> keys = propertiesFileChecker.getInfos("liferay-versions");

			if (keys != null && keys.size() > 0) {
				String versions = keys.get(0).value;

				if (!versions.matches(".*7\\.[0-9]\\.[0-9].*")) {
					List<SearchResult> results = propertiesFileChecker.findProperties("liferay-versions");

					if (results != null) {
						String sectionHtml = _problemSummary;

						for (SearchResult searchResult : results) {
							searchResult.autoCorrectContext = PREFIX + "liferay-versions";

							problems.add(new Problem( _problemTitle, _problemSummary,
								_problemType, _problemTickets, file,
								searchResult.startLine, searchResult.startOffset,
								searchResult.endOffset, sectionHtml, searchResult.autoCorrectContext,
								Problem.STATUS_NOT_RESOLVED));
						}
					}
				}
			}
		}

		return problems;
	}

	@Override
	public void correctProblems(File file, List<Problem> problems) throws AutoMigrateException {
		try {
			String contents = new String(IO.read(file));

			final JavaFile javaFile = _context.getBundleContext()
					.getService(_context.getBundleContext().getServiceReference(JavaFile.class));
			final IFile propertiesFile = javaFile.getIFile(file);

			for (Problem problem : problems) {
				if (problem.autoCorrectContext instanceof String) {
					final String propertyData = problem.autoCorrectContext;

					if (propertyData != null && propertyData.startsWith(PREFIX)) {
						final String propertyValue = propertyData.substring(PREFIX.length());

						contents = contents.replaceAll(propertyValue+".*", propertyValue + "=7.0.0+");
					}
				}
			}

			propertiesFile.setContents(new ByteArrayInputStream(contents.getBytes()), IResource.FORCE, null);

		} catch (CoreException | IOException e) {
			e.printStackTrace();
		}
	}

}
