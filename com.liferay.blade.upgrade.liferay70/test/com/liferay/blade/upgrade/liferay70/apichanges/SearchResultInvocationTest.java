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
import com.liferay.blade.upgrade.liferay70.apichanges.SearchResultInvocation;

@SuppressWarnings("restriction")
public class SearchResultInvocationTest {
	final File testFile = new File( "projects/filetests/SearchResultTest.java" );
	SearchResultInvocation component;

	@Before
	public void beforeTest() {
		assertTrue( testFile.exists() );
		component = new SearchResultInvocation();
	}

	@Test
	public void dlFileEntryTypeLocalServiceTest() throws Exception {
		List<SearchResult> results =
			component.searchFile(testFile, new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(3, results.size());
	}

	@Test
	public void dlFileEntryTypeLocalServiceTestTwice() throws Exception {
		List<SearchResult> results =
			component.searchFile(testFile, new JavaFileJDT(testFile));

		results =
			component.searchFile(testFile, new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(3, results.size());
	}

}
