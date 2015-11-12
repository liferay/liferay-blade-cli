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
public class PortalUtilGetClassNamePortletIdInvocationTest {
	final File testFile = new File(
			"projects/test-ext/docroot/WEB-INF/ext-impl/src/com/liferay/test/PortalUtilTest.java");
	PortalUtilGetClassNamePortletIdInvocation component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new PortalUtilGetClassNamePortletIdInvocation();
	}

	@Test
	public void portalUtilGetClassNamePortletIdAnalyze() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

        assertNotNull(results);
        assertEquals(1, results.size());
	}

	@Test
	public void portalUtilGetClassNamePortletIdAnalyzeTwice()
			throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

        assertNotNull(results);
        assertEquals(1, results.size());
	}
}
