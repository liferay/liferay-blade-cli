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
import com.liferay.blade.upgrade.liferay70.apichanges.WebContentArticlesStrucAndTempl;

@SuppressWarnings("restriction")
public class WebContentArticlesStrucAndTemplTest {

	final File testFile = new File(
			"projects/filetests/WebContentArticlesStrucAndTemplTestFile.java");
	WebContentArticlesStrucAndTempl component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new WebContentArticlesStrucAndTempl();
	}

	@Test
	public void reservedUserIdExceptionJavaTest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(2, results.size());
	}

}
