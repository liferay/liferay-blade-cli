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
import com.liferay.blade.upgrade.liferay70.apichanges.WebContentTypeRemoved;

@SuppressWarnings("restriction")
public class WebContentTypeRemovedTest {

	final File testFile = new File(
			"projects/filetests/WebContentTypeRemovedTestFile.java");
	WebContentTypeRemoved component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new WebContentTypeRemoved();
	}

	@Test
	public void webContentTypeRemovedTest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(8, results.size());
	}

}
