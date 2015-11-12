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
public class MarketplaceLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/MarketplaceMessageListener.java");
	MarketplaceLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new MarketplaceLegacyAPI();
	}

	@Test
	public void marketplaceLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(6, results.size());

		SearchResult problem = results.get(0);

		assertEquals(18, problem.startLine);
		assertEquals(664, problem.startOffset);
		assertEquals(715, problem.endOffset);

		problem = results.get(1);

		assertEquals(60, problem.startLine);
		assertEquals(2009, problem.startOffset);
		assertEquals(2115, problem.endOffset);

		problem = results.get(2);

		assertEquals(87, problem.startLine);
		assertEquals(2801, problem.startOffset);
		assertEquals(2861, problem.endOffset);

		problem = results.get(3);

		assertEquals(19, problem.startLine);
		assertEquals(724, problem.startOffset);
		assertEquals(778, problem.endOffset);

		problem = results.get(4);

		assertEquals(73, problem.startLine);
		assertEquals(2431, problem.startOffset);
		assertEquals(2539, problem.endOffset);

		problem = results.get(5);

		assertEquals(82, problem.startLine);
		assertEquals(2683, problem.startOffset);
		assertEquals(2792, problem.endOffset);
	}

}
