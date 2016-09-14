/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.upgrade.liferay70;

import com.liferay.blade.api.AutoMigrateException;
import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.SearchResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;

public abstract class ImportStatementMigrator extends AbstractFileMigrator<JavaFile> implements AutoMigrator {

	private static final String PREFIX = "import:";
	private final Map<String, String> _imports = new HashMap<>();

	public ImportStatementMigrator(String[] imports, String[] fixedImports) {
		super(JavaFile.class);

		for(int i = 0; i < imports.length; i++) {
			_imports.put(imports[i], fixedImports[i]);
		}
	}

	@Override
	public void correctProblems(File file, List<Problem> problems) throws AutoMigrateException {
		final List<String> importsToRewrite = new ArrayList<>();

		for (Problem problem : problems) {
			if (problem.autoCorrectContext instanceof String) {
				final String importData = problem.autoCorrectContext;

				if (importData != null && importData.startsWith(PREFIX)) {
					final String importValue = importData.substring(PREFIX.length());

					if (_imports.containsKey(importValue)) {
						importsToRewrite.add(importValue);
					}
				}
			}
		}

		if (importsToRewrite.size() > 0) {
			try {
				FileInputStream inputStream = new FileInputStream(file);
				String[] lines = readLines(inputStream);
				inputStream.close();

				String[] editedLines = new String[lines.length];
				System.arraycopy(lines, 0, editedLines, 0, lines.length);

				for (String importToRewrite : importsToRewrite) {
					for (int i = 0; i < editedLines.length; i++) {
						editedLines[i] = editedLines[i].replace("import " + importToRewrite, "import " + _imports.get(importToRewrite));
					}
				}

				StringBuilder sb = new StringBuilder();
				for (String editedLine : editedLines) {
					sb.append(editedLine);
					sb.append(System.getProperty("line.separator"));
				}

				FileWriter writer = new FileWriter(file);
				writer.write(sb.toString());
				writer.close();
			} catch (IOException e) {
				throw new AutoMigrateException("Unable to auto-correct", e);
			}
		}
	}

	protected IFile getJavaFile(File file) {
		final JavaFile javaFileService = _context.getService(_context.getServiceReference(JavaFile.class));

		return javaFileService.getIFile(file);
	}

	private static String[] readLines(InputStream inputStream) {
		if (inputStream == null) {
			return null;
		}

		List<String> lines = new ArrayList<>();

		try (BufferedReader bufferedReader =
				new BufferedReader(new InputStreamReader(inputStream))) {

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				StringBuffer contents = new StringBuffer(line);

				lines.add(contents.toString());
			}
		}
		catch (Exception e) {
		}

		return lines.toArray(new String[lines.size()]);
	}

	@Override
	public List<SearchResult> searchFile(File file, JavaFile javaFile) {
		final List<SearchResult> searchResults = new ArrayList<>();

		for (String importName : _imports.keySet()) {
			final SearchResult importResult = javaFile.findImport(importName);

			if (importResult != null) {
				importResult.autoCorrectContext = PREFIX + importName;

				searchResults.add(importResult);
			}
		}

		return searchResults;
	}

	public static String getPrefix() {
		return PREFIX;
	}

	public Map<String, String> getImports() {
		return _imports;
	}

}
