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
import com.liferay.blade.upgrade.liferay70.apichanges.UserServicesThrownExceptionsChanges;

@SuppressWarnings("restriction")
public class UserServicesThrownExceptionsChangesTest {
	final File testFile = new File(
			"projects/filetests/UserServicesThrownExceptionsChangesTest.java");
	UserServicesThrownExceptionsChanges component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new UserServicesThrownExceptionsChanges();
	}

	@Test
	public void userServicesThrownExceptionsChanges() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(2, results.size());
	}

	@Test
	public void userServicesThrownExceptionsChangesTestTwice() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(2, results.size());
	}
}
