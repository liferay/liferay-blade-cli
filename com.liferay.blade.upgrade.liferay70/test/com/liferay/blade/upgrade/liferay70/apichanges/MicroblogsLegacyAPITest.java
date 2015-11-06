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
import com.liferay.blade.upgrade.liferay70.apichanges.MicroblogsLegacyAPI;

@SuppressWarnings("restriction")
public class MicroblogsLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/MicroblogsPortlet.java");
	MicroblogsLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new MicroblogsLegacyAPI();
	}

	@Test
	public void microblogsLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(4, results.size());

		SearchResult problem = results.get(0);

		assertEquals(22, problem.startLine);
		assertEquals(951, problem.startOffset);
		assertEquals(1008, problem.endOffset);

		problem = results.get(1);

		assertEquals(47, problem.startLine);
		assertEquals(1763, problem.startOffset);
		assertEquals(1830, problem.endOffset);

		problem = results.get(2);

		assertEquals(77, problem.startLine);
		assertEquals(2803, problem.startOffset);
		assertEquals(2920, problem.endOffset);

		problem = results.get(3);

		assertEquals(81, problem.startLine);
		assertEquals(2938, problem.startOffset);
		assertEquals(3112, problem.endOffset);
	}

}
