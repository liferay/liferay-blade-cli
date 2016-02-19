/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
