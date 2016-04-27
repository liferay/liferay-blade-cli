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

package com.liferay.blade.api;

import java.io.File;

public class SearchResult{

	public final int endLine;
	public final int endOffset;
	public final File file;
	public final boolean fullMatch;
	public final int startLine;
	public final int startOffset;
	public String autoCorrectContext;

	public SearchResult(File file, int startOffset, int endOffset,
			int startLine, int endLine, boolean fullMatch) {

		this.file = file;
		this.fullMatch = fullMatch;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SearchResult) {
			SearchResult sr = (SearchResult) obj;

			if (startLine == sr.startLine && startOffset == sr.startOffset 
					&& endOffset == sr.endOffset) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

}