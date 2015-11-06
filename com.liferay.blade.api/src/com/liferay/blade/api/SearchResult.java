package com.liferay.blade.api;

import java.io.File;

public class SearchResult {

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

}