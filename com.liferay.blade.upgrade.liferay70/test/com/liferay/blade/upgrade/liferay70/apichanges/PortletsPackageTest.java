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
public class PortletsPackageTest {
	final File file = new File("projects/filetests/PortletsPackageTest.java");
	PortletsPackage component;

	@Before
	public void beforeTest() {
		assertTrue(file.exists());
		component = new PortletsPackage();
	}

	@Test
	public void portalPropertiesAnalyzeTest() throws Exception {
		List<SearchResult> problems = component.searchFile(file,
				new JavaFileJDT(file));

		assertNotNull(problems);
		assertEquals(1, problems.size());
	}

	@Test
	public void portalPropertiesAnalyzeTest2() throws Exception {
		List<SearchResult> problems = component.searchFile(file,
				new JavaFileJDT(file));
		problems = component.searchFile(file, new JavaFileJDT(file));

		assertNotNull(problems);
		assertEquals(1, problems.size());
	}

}
