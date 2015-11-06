package com.liferay.blade.api;

import java.util.List;

public interface JSPFile extends JavaFile {

	List<SearchResult> findJSPTags(String tagName , String[] attrNames , String[] attrValues);

}
