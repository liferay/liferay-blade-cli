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
import com.liferay.blade.upgrade.liferay70.apichanges.RepositoryServiceUtilGetRepositoryImplInvocation;

@SuppressWarnings("restriction")
public class RepositoryServiceUtilGetRepositoryImplInvocationTest {
	final File testFile = new File(
			"projects/filetests/RepositoryServiceUtilTest.java");

	final File jspFile = new File(
			"projects/filetests/RepositoryServiceUtilTest.jsp");

	RepositoryServiceUtilGetRepositoryImplInvocation component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new RepositoryServiceUtilGetRepositoryImplInvocation();
	}

	@Test
	public void repositoryServiceUtilAnalyzeTest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}

	@Test
	public void repositoryServiceUtilTestTwice() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));
		results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}

}
