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
import com.liferay.blade.upgrade.liferay70.apichanges.DLAppHelperLocalServiceUtilInvocation;

@SuppressWarnings("restriction")
public class DLAppHelperLocalServiceUtilInvocationTest {
	final File testFile = new File( "projects/test-ext/docroot/WEB-INF/ext-impl/src/com/liferay/test/DLAppHelperLocalServiceUtilTest.java" );
	DLAppHelperLocalServiceUtilInvocation component;

	@Before
	public void beforeTest() {
		assertTrue( testFile.exists() );
		component = new DLAppHelperLocalServiceUtilInvocation();
	}

    @Test
    public void dlAppHelperLocalServiceTest() throws Exception {
    	List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(5, results.size());
    }

    @Test
    public void dlAppHelperLocalServiceTestTwice() throws Exception {
    	List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

    	results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(5, results.size());
    }
}
