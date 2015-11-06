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
import com.liferay.blade.upgrade.liferay70.apichanges.DDMLegacyAPI;

@SuppressWarnings("restriction")
public class DDMLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/JournalArticleAssetRendererFactory.java");
	DDMLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new DDMLegacyAPI();
	}

	@Test
	public void dDMLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(5, results.size());

		SearchResult problem = results.get(0);

		assertEquals(36, problem.startLine);
		assertEquals(1669, problem.startOffset);
		assertEquals(1744, problem.endOffset);

		problem = results.get(1);

		assertEquals(134, problem.startLine);
		assertEquals(4696, problem.startOffset);
		assertEquals(4753, problem.endOffset);

		problem = results.get(2);

		assertEquals(147, problem.startLine);
		assertEquals(5031, problem.startOffset);
		assertEquals(5088, problem.endOffset);

		problem = results.get(3);

		assertEquals(37, problem.startLine);
		assertEquals(1753, problem.startOffset);
		assertEquals(1823, problem.endOffset);

		problem = results.get(4);

		assertEquals(162, problem.startLine);
		assertEquals(5412, problem.startOffset);
		assertEquals(5527, problem.endOffset);

	}

}
