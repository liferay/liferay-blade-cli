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
import com.liferay.blade.upgrade.liferay70.apichanges.DDMTemplateUpdateTemplateInvocation;

@SuppressWarnings("restriction")
public class DDMTemplateUpdateTemplateInvocationTest {
	final File testFile = new File(
			"projects/filetests/DDMTemplateLocalServiceUtilTest.java");
	DDMTemplateUpdateTemplateInvocation component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new DDMTemplateUpdateTemplateInvocation();
	}

	@Test
	public void ddmTemplateAnalyzeTest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}

	@Test
	public void ddmTemplateAnalyzeTestTwice() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}
}
