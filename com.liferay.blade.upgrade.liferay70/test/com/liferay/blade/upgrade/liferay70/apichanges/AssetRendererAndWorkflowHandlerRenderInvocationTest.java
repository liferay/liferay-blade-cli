
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
import com.liferay.blade.upgrade.liferay70.apichanges.AssetRendererAndWorkflowHandlerRenderInvocation;

@SuppressWarnings("restriction")
public class AssetRendererAndWorkflowHandlerRenderInvocationTest {
	final File testFile = new File("projects/filetests/RenderTest.java");
	AssetRendererAndWorkflowHandlerRenderInvocation component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new AssetRendererAndWorkflowHandlerRenderInvocation();
	}

	@Test
	public void assetRenderTest() throws Exception {
		List<SearchResult> problems = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(problems);
		assertEquals(6, problems.size());
	}

	@Test
	public void assetRenderTestTwice() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(6, results.size());
	}
}
