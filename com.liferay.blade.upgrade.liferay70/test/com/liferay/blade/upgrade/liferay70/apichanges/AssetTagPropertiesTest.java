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
public class AssetTagPropertiesTest {

	final File testFile = new File("projects/filetests/MediaWikiImporter.java");
	final File testFile2 = new File(
			"projects/filetests/AssetTagPropertiesTestFile.java");
	AssetTagProperties component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		assertTrue(testFile2.exists());
		component = new AssetTagProperties();
	}

	@Test
	public void assetTagPropertiesTest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(2, results.size());
	}

	@Test
	public void assetTagPropertiesAnotherTest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile2,
				new JavaFileJDT(testFile2));

		assertNotNull(results);
		assertEquals(4, results.size());
	}

}
