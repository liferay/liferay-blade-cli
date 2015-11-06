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
import com.liferay.blade.upgrade.liferay70.apichanges.WikiUtilGetEntriesInvocation;

@SuppressWarnings("restriction")
public class WikiUtilGetEntriesInvocationTest {
	final File testFile = new File(
			"projects/test-ext/docroot/WEB-INF/ext-impl/src/com/liferay/test/WikiUtilTest.java");
	WikiUtilGetEntriesInvocation component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new WikiUtilGetEntriesInvocation();
	}

	@Test
	public void wikiUtilGetEntriesInvocation() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}

	@Test
	public void wikiUtilGetEntriesInvocationTwice() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}
}
