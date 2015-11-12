package com.liferay.blade.eclipse.provider;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class XMLFileSAXTest {


	@Test
	public void offlineSupport() throws Exception {
		File file = new File("tests/files/service.xml");

		assertEquals(1, new XMLFileSAX(file).findTag("service-builder", null).size());
	}

	@Test
	public void elementContent() throws Exception {
		File file = new File("tests/files/service.xml");

		assertEquals(1, new XMLFileSAX(file).findTag("namespace", "KB").size());
	}

}
