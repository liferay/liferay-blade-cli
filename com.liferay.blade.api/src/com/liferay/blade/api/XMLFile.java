package com.liferay.blade.api;

import java.util.Collection;

public interface  XMLFile extends SourceFile {

	Collection<SearchResult> findTag(String elementName, String elementValue);
}
