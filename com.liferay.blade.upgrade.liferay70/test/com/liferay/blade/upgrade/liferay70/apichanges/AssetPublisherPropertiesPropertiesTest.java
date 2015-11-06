package com.liferay.blade.upgrade.liferay70.apichanges;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.liferay.blade.api.Problem;
import com.liferay.blade.upgrade.liferay70.apichanges.AssetPublisherProperties;

public class AssetPublisherPropertiesPropertiesTest {
	final File file =
		new File("projects/test-portlet/docroot/WEB-INF/src/portal.properties");
	AssetPublisherProperties component;

	@Before
	public void beforeTest() {
		assertTrue(file.exists());
		component = new AssetPublisherProperties();
		component.addPropertiesToSearch(component._properties);
	}

	@Test
	public void assetPublisherPropertiesAnalyzeTest() throws Exception {
		List<Problem> problems = component.analyze(file);

		assertNotNull(problems);
		assertEquals(1, problems.size());
	}

	@Test
	public void assetPublisherAnalyzeTest2() throws Exception {
		List<Problem> problems = component.analyze(file);
		problems = component.analyze(file);

		assertNotNull(problems);
		assertEquals(1, problems.size());
	}

}
