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
import com.liferay.blade.upgrade.liferay70.apichanges.MobileDeviceRulesLegacyAPI;

@SuppressWarnings("restriction")
public class MobileDeviceRulesLegacyAPITest {
	final File testFile = new File(
			"projects/legacy-apis-ant-portlet/docroot/WEB-INF/src/com/liferay/EditLayoutsAction.java");
	MobileDeviceRulesLegacyAPI component;

	@Before
	public void beforeTest() {
		assertTrue(testFile.exists());
		component = new MobileDeviceRulesLegacyAPI();
	}

	@Test
	public void mobileDeviceRulesLegacyAPITest() throws Exception {
		List<SearchResult> results = component.searchFile(testFile,
				new JavaFileJDT(testFile));

		assertNotNull(results);
		assertEquals(8, results.size());

		SearchResult problem = results.get(0);

		assertEquals(36, problem.startLine);
		assertEquals(1519, problem.startOffset);
		assertEquals(1590, problem.endOffset);

		problem = results.get(1);

		assertEquals(64, problem.startLine);
		assertEquals(2741, problem.startOffset);
		assertEquals(2836, problem.endOffset);

		problem = results.get(2);

		assertEquals(37, problem.startLine);
		assertEquals(1599, problem.startOffset);
		assertEquals(1665, problem.endOffset);

		problem = results.get(3);

		assertEquals(68, problem.startLine);
		assertEquals(2893, problem.startOffset);
		assertEquals(3112, problem.endOffset);

		problem = results.get(4);

		assertEquals(38, problem.startLine);
		assertEquals(1674, problem.startOffset);
		assertEquals(1756, problem.endOffset);

		problem = results.get(5);

		assertEquals(50, problem.startLine);
		assertEquals(2199, problem.startOffset);
		assertEquals(2310, problem.endOffset);

		problem = results.get(6);

		assertEquals(39, problem.startLine);
		assertEquals(1765, problem.startOffset);
		assertEquals(1842, problem.endOffset);

		problem = results.get(7);

		assertEquals(57, problem.startLine);
		assertEquals(2457, problem.startOffset);
		assertEquals(2696, problem.endOffset);
	}

}
