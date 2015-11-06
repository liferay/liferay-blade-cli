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
import com.liferay.blade.upgrade.liferay70.apichanges.MVCPortletInitParamsChangeClass;

/**
 * @author Andy Wu
 */
@SuppressWarnings("restriction")
public class MVCPortletInitParamsChangeClassTest {

	@Before
	public void setUp() {
		assertTrue(testFile.exists());
		component = new MVCPortletInitParamsChangeClass();
	}

	@Test
	public void testMVCPortletChangeExtendsTest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

        assertNotNull(results);
        assertEquals(1, results.size());
	}

	private MVCPortletInitParamsChangeClass component;
	private final File testFile = new File(
		"projects/knowledge-base-portlet-6.2.x/docroot/"+
		"WEB-INF/src/com/liferay/knowledgebase/portlet/BaseKBPortlet.java");

}
