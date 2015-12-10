package com.liferay.blade.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.liferay.blade.api.Migration;
import com.liferay.blade.api.Problem;
import com.liferay.blade.util.NullProgressMonitor;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class CustomAUIValidatorsTagsTest {

	@Test
	public void findProblems() throws Exception {
		ServiceReference<Migration> sr = context
				.getServiceReference(Migration.class);

		Migration m = context.getService(sr);

		List<Problem> problems = m.findProblems(new File("jsptests/custom-aui-validators/"), new NullProgressMonitor());

		assertEquals(1, problems.size());

		boolean found = false;

		for (Problem problem : problems) {
			if (problem.file.getName().endsWith("CustomAUIValidatorsTest.jsp")
					&& problem.lineNumber == 314 && problem.startOffset == 11381
					&& problem.endOffset == 11394) {

				found = true;
			}
		}

		if (!found) {
			fail();
		}

	}

	private final BundleContext context = FrameworkUtil.getBundle(
		this.getClass()).getBundleContext();

}