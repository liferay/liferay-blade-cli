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
public class ConfigurationActionRenderMethodTest {

	final File configurationActionImplFile = new File( "projects/opensocial-portlet-6.2.x/docroot/WEB-INF/src/com/liferay/opensocial/gadget/action/ConfigurationActionImpl.java" );
	final File editConfigurationActionFile = new File( "projects/filetests/EditConfigurationAction.java" );
	ConfigurationActionRenderMethod component;

	@Before
	public void beforeTest() {
		assertTrue(configurationActionImplFile.exists());
		component = new ConfigurationActionRenderMethod();
	}

	@Test
	public void configurationActionImplFile() throws Exception {
		List<SearchResult> results = component.searchFile(configurationActionImplFile,
				new JavaFileJDT(configurationActionImplFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}

	@Test
	public void editConfigurationActionFile() throws Exception {
		List<SearchResult> results = component.searchFile(editConfigurationActionFile,
				new JavaFileJDT(editConfigurationActionFile));

		assertNotNull(results);
		assertEquals(1, results.size());
	}

}
