package com.liferay.blade.eclipse.provider;

import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.Reporter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import dnl.utils.text.table.TextTable;

@Component(
	property = {
		Constants.SERVICE_RANKING + ":Integer=0",
		"format:String=text"
	},
	service = Reporter.class
)
public class TextReporter extends ConsoleReporter {

	private OutputStream _output;

	@Override
	public void beginReporting(int format, OutputStream output) {
		_output = output;

		if (format == Migration.DETAIL_SHORT) {
			columnNames.add("Title");
			columnNames.add("Type");
			columnNames.add("File");
			columnNames.add("Line");
		}
		else {
			columnNames.add("Title");
			columnNames.add("Summary");
			columnNames.add("Type");
			columnNames.add("Ticket");
			columnNames.add("File");
			columnNames.add("Line");
		}
	}

	@Override
	public void endReporting() {
		Object[][] data = new Object[rowData.size()][columnNames.size()];

		for (int i = 0; i < rowData.size(); i++) {
			for (int j = 0; j < columnNames.size(); j++) {
				data[i][j] = rowData.get(i)[j];
			}
		}

		TextTable tt = new TextTable(columnNames.toArray(new String[0]), data);
		tt.setAddRowNumbering(true);
		tt.setSort(0);
		tt.printTable(new PrintStream(_output), 0);

		this.columnNames.clear();
		this.rowData.clear();

		try {
			if (_output != null) {
				_output.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void report(Problem problem) {
		if (columnNames.size() == 4) {
			rowData.add(new Object[] { problem.title, problem.type,
					problem.file.getName(), problem.lineNumber
				});
		} else {
			rowData.add(new Object[] { problem.title, problem.summary,
					problem.type, problem.ticket,
					problem.file.getName(), problem.lineNumber
				});
		}
	}

	private final List<String> columnNames = new ArrayList<>();
	private final List<Object[]> rowData = new ArrayList<>();

}