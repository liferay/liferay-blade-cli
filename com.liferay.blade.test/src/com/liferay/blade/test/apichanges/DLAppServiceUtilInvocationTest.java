package com.liferay.blade.test.apichanges;

import java.io.File;

public class DLAppServiceUtilInvocationTest extends APITestBase{

	@Override
	public int getExpectedNumber() {
		return 11;
	}

	@Override
	public String getImplClassName() {
		return "DLAppServiceUtilInvocation";
	}

	@Override
	public File getTestFile() {
		return new File("projects/test-ext/docroot/WEB-INF/ext-impl/src/com/liferay/test/DLAppServiceUtilTest.java");
	}

}
