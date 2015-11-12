package com.liferay.blade.upgrade.liferay70.apichanges;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.liferay.blade.api.SearchResult;
import com.liferay.blade.eclipse.provider.JavaFileJDT;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("restriction")
public class DDLRecordLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/EditRecordAction.java");
	DDLRecordLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new DDLRecordLegacyAPI();
	}

	@Test
	public void dDLRecordLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(3, results.size());

		SearchResult problem = results.get(0);

		assertEquals(30, problem.startLine);
		assertEquals(1332, problem.startOffset);
		assertEquals(1397, problem.endOffset);

		problem = results.get(1);

		assertEquals(132, problem.startLine);
		assertEquals(4089, problem.startOffset);
		assertEquals(4132, problem.endOffset);

		problem = results.get(2);

		assertEquals(145, problem.startLine);
		assertEquals(4475, problem.startOffset);
		assertEquals(4554, problem.endOffset);

	}

}
