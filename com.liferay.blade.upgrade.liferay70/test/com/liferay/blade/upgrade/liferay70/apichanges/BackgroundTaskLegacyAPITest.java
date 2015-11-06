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
import com.liferay.blade.upgrade.liferay70.apichanges.BackgroundTaskLegacyAPI;

@SuppressWarnings("restriction")
public class BackgroundTaskLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/DLFileNameBackgroundTaskServiceImpl.java");
	BackgroundTaskLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new BackgroundTaskLegacyAPI();
	}

	@Test
	public void backgroundTaskLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(2, results.size());

		SearchResult problem = results.get(0);

		assertEquals(18, problem.startLine);
		assertEquals(671, problem.startOffset);
		assertEquals(719, problem.endOffset);

		problem = results.get(1);

		assertEquals(19, problem.startLine);
		assertEquals(728, problem.startOffset);
		assertEquals(783, problem.endOffset);

	}

}
