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
import com.liferay.blade.upgrade.liferay70.apichanges.MVCPortletActionCommandImports;

@SuppressWarnings("restriction")
public class ActionCommandImportsTest {

	final File sayHelloActionCommandFile = new File(
			"projects/actioncommand-demo-portlet/docroot/WEB-INF/src/com/liferay/demo/portlet/action/SayHelloActionCommand.java");
	final File sayHelloActionCommandFile2 = new File(
			"projects/actioncommand-demo-portlet/docroot/WEB-INF/src/com/liferay/demo/portlet/action/SayHelloActionCommand2.java");
	MVCPortletActionCommandImports component;

	@Before
	public void beforeTest() {
		assertTrue(sayHelloActionCommandFile.exists());
		component = new MVCPortletActionCommandImports();
	}

	@Test
	public void sayHelloActionCommandFile() throws Exception {
		List<SearchResult> results = component.searchFile(sayHelloActionCommandFile,
				new JavaFileJDT(sayHelloActionCommandFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}

	@Test
	public void sayHelloActionCommandFile2() throws Exception {
		List<SearchResult> results = component.searchFile(sayHelloActionCommandFile2,
				new JavaFileJDT(sayHelloActionCommandFile2));

		assertNotNull(results);
		assertEquals(2, results.size());
	}

	@Test
	public void sayHelloActionCommandFile2x() throws Exception {
		List<SearchResult> results = component.searchFile(sayHelloActionCommandFile,
				new JavaFileJDT(sayHelloActionCommandFile));

		component.searchFile(sayHelloActionCommandFile,
				new JavaFileJDT(sayHelloActionCommandFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}
}
