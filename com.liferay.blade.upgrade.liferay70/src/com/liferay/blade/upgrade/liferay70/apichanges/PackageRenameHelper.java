package com.liferay.blade.upgrade.liferay70.apichanges;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PackageRenameHelper {

	private String[] _oldImports = null;
	private String[] _newImports = null;

	public String[] getOldImports() {
		if (_oldImports != null) {
			return _oldImports;
		}

		String[][] packageChangeMap = _readCSV();
		_oldImports = new String[packageChangeMap.length];

		for (int i = 0; i < packageChangeMap.length; i++) {
			_oldImports[i] = packageChangeMap[i][0];
		}

		return _oldImports;
	}

	public String[] getNewImports() {
		if (_newImports != null) {
			return _newImports;
		}

		String[][] packageChangeMap = _readCSV();
		_newImports = new String[packageChangeMap.length];

		for (int i = 0; i < packageChangeMap.length; i++) {
			_newImports[i] = packageChangeMap[i][1];
		}

		return _newImports;
	}

	public String[][] _readCSV() {
		try (InputStream in = getClass().getResourceAsStream("/com/liferay/blade/upgrade/liferay70/apichanges/kernel-rename.csv")) {
			List<String> lines = new ArrayList<>();

			try (BufferedReader bufferedReader =
					new BufferedReader(new InputStreamReader(in))) {

				String line;

				while ((line = bufferedReader.readLine()) != null) {
					StringBuffer contents = new StringBuffer(line);

					lines.add(contents.toString());
				}
			}
			catch (Exception e) {
			}

			String[] lineArray = lines.toArray(new String[lines.size()]);

			String[][] results = new String[lineArray.length][2];

			for (int i = 0; i < lineArray.length; i++) {
				String line = lineArray[i];

				String[] columns = line.split(",");

				results[i][0] = columns[0];
				results[i][1] = columns[1];
			}

			return results;

		} catch (IOException e) {
		}

		return null;
	}

}
