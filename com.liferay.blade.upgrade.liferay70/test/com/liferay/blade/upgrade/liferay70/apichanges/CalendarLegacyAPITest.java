package com.liferay.blade.upgrade.liferay70.apichanges;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.liferay.blade.api.SearchResult;
import com.liferay.blade.eclipse.provider.JavaFileJDT;
import com.liferay.blade.upgrade.liferay70.apichanges.CalendarLegacyAPI;

@SuppressWarnings("restriction")
public class CalendarLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/CalendarPortlet.java");
	CalendarLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new CalendarLegacyAPI();
	}

	@Test
	public void calendarLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(7, results.size());

		SearchResult problem = results.get(0);

		assertEquals(38, problem.startLine);
		assertEquals(1849, problem.startOffset);
		assertEquals(1909, problem.endOffset);

		problem = results.get(1);

		assertEquals(39, problem.startLine);
		assertEquals(1918, problem.startOffset);
		assertEquals(1973, problem.endOffset);

		problem = results.get(2);

		assertEquals(40, problem.startLine);
		assertEquals(1982, problem.startOffset);
		assertEquals(2035, problem.endOffset);

		problem = results.get(3);

		assertEquals(159, problem.startLine);
		assertEquals(6848, problem.startOffset);
		assertEquals(6983, problem.endOffset);

		problem = results.get(4);

		assertEquals(43, problem.startLine);
		assertEquals(2186, problem.startOffset);
		assertEquals(2234, problem.endOffset);

		problem = results.get(5);

		assertEquals(41, problem.startLine);
		assertEquals(2044, problem.startOffset);
		assertEquals(2112, problem.endOffset);

		problem = results.get(6);

		assertEquals(42, problem.startLine);
		assertEquals(2121, problem.startOffset);
		assertEquals(2177, problem.endOffset);

	}

}
