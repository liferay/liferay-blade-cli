
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
import com.liferay.blade.upgrade.liferay70.apichanges.MBMessageServiceUtilInvocation;

@SuppressWarnings("restriction")
public class MBMessageServiceUtilInvocationTest {
	final File testFile = new File( "projects/filetests/EditDiscussionAction.java" );
	MBMessageServiceUtilInvocation component;

	@Before
	public void beforeTest() {
		assertTrue( testFile.exists() );
		component = new MBMessageServiceUtilInvocation();
	}

    @Test
    public void dlAppHelperLocalServiceTest() throws Exception {
    	List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

        assertNotNull(results);
        assertEquals(3, results.size());

        SearchResult problem = results.get(0);

		assertEquals( 248, problem.startLine );
		assertEquals( 7830, problem.startOffset );
		assertEquals( 8058, problem.endOffset );
    }

    @Test
    public void dlAppHelperLocalServiceTestTwice() throws Exception {
    	List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

    	results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

        assertNotNull(results);
        assertEquals(3, results.size());
    }
}
