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
public class ScreenNameValidatorInterfaceTest {
	final File testFile = new File("projects/filetests/DefaultScreenNameValidator.java");
	ScreenNameValidatorInterface component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new ScreenNameValidatorInterface();
	}

	@Test
	public void screenNameValidatorInterface() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}
}
