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

public class RepositoryServiceUtilTest {

	@Test
	public void repositoryServiceUtilTest() throws Exception {
		ServiceReference<Migration> sr = context
				.getServiceReference(Migration.class);

		Migration m = context.getService(sr);

		List<Problem> problems = m
				.findProblems(new File(
						"jsptests/repository-service-util"), new NullProgressMonitor());

		assertEquals(1, problems.size());

		boolean found = false;

		for (Problem problem : problems) {
			if (problem.file.getName().endsWith("RepositoryServiceUtilTest.jsp")) {
				if (problem.lineNumber == 9 && problem.startOffset == 104 && problem.endOffset == 171) {
					found = true;
				}
			}
		}

		if (!found) {
			fail();
		}
	}

	private final BundleContext context = FrameworkUtil.getBundle(
		this.getClass()).getBundleContext();

}
