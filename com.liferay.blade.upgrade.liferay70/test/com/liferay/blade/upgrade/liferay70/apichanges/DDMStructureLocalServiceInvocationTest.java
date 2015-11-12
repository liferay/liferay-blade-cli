
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
public class DDMStructureLocalServiceInvocationTest {
	final File testFile = new File( "projects/filetests/DDMStructureLocalServiceAPITest.java" );
	DDMStructureLocalServiceAPI component;

	@Before
	public void beforeTest() {
		assertTrue( testFile.exists() );
		component = new DDMStructureLocalServiceAPI();
	}

    @Test
    public void ddmTemplateAnalyzeTest() throws Exception {
    	List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

        assertNotNull(results);
        assertEquals(1, results.size());

        SearchResult problem = results.get(0);

        assertEquals(7, problem.startLine);
        assertEquals(138, problem.startOffset);
        assertEquals(264, problem.endOffset);
    }

}
