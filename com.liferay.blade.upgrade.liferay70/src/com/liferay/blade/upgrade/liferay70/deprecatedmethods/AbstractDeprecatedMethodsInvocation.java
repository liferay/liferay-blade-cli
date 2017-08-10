package com.liferay.blade.upgrade.liferay70.deprecatedmethods;

import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;
import com.liferay.blade.upgrade.liferay70.JavaFileMigrator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.Path;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class AbstractDeprecatedMethodsInvocation extends JavaFileMigrator {

	private static JSONObject tempMethod = null;

	@Override
	public List<Problem> analyze(File file) {
		final List<Problem> problems = new ArrayList<>();

		JSONArray DeprecatedMethods = getDeprecatedMethods();

		for (int i = 0; i < DeprecatedMethods.length(); i++) {
			tempMethod = DeprecatedMethods.getJSONObject(i);

			final List<SearchResult> searchResults = searchFile(file, createFileChecker(_type, file));

			if (searchResults != null) {
				for (SearchResult searchResult : searchResults) {
					String fileExtension = new Path(file.getAbsolutePath()).getFileExtension();

					problems.add(new Problem(tempMethod.getString("javadoc"), tempMethod.getString("javadoc"),
							fileExtension, "", file, searchResult.startLine, searchResult.startOffset,
							searchResult.endOffset, tempMethod.getString("javadoc"), searchResult.autoCorrectContext,
							Problem.STATUS_NOT_RESOLVED, Problem.DEFAULT_MARKER_ID));
				}
			}
		}

		return problems;
	}

	public JSONArray getDeprecatedMethods() {
		String jsonFilePath = getJsonFilePath();

		try (InputStream in = getClass().getResourceAsStream(jsonFilePath)) {

			String jsonContext = IOUtils.toString(in, "UTF-8");

			return new JSONArray(jsonContext);
		}
		catch (IOException e) {
		}

		return null;
	}

	protected abstract String getJsonFilePath();

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile fileChecker) {
		final List<SearchResult> searchResults = new ArrayList<>();

		String[] parameters = null;

		JSONArray parameterJSONArray = tempMethod.getJSONArray("parameters");

		if (parameterJSONArray != null) {
			parameters = new String[parameterJSONArray.length()];

			for (int i = 0; i < parameterJSONArray.length(); i++) {
				parameters[i] = parameterJSONArray.getString(i);
			}
		}

		searchResults.addAll(fileChecker.findMethodInvocations(
			tempMethod.getString("className"), null, tempMethod.getString("methodName"), parameters));

		searchResults.addAll(fileChecker.findMethodInvocations(
			null, tempMethod.getString("className"), tempMethod.getString("methodName"), parameters));

		return searchResults;
	}

}