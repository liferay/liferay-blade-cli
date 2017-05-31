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

package com.liferay.blade.test.apichanges;

import java.io.File;

public class DeprecatedAUIToolTagsTest extends APITestBase {

	@Override
	public int getExpectedNumber() {
		return 1;
	}

	@Override
	public String getImplClassName() {
		return "DeprecatedAUIToolTags";
	}

	@Override
	public File getTestFile() {
		return new File("jsptests/aui-tool/AUIToolTagTest.jsp");
	}

}
