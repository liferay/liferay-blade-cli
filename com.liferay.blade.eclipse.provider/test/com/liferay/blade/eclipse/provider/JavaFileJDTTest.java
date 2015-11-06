package com.liferay.blade.eclipse.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.liferay.blade.api.SearchResult;
import com.liferay.blade.eclipse.provider.JavaFileJDT;

public class JavaFileJDTTest {

	@Test
	public void checkStaticMethodInvocation() throws Exception {
		File file = new File( "tests/files/JavaFileChecker.java" );
		JavaFileJDT javaFileChecker = new JavaFileJDT(file);
		List<SearchResult> searchResults = javaFileChecker.findMethodInvocations(null, "String", "valueOf", null);

		assertNotNull(searchResults);

		SearchResult searchResult = searchResults.get(0);

		assertNotNull(searchResult);
		assertEquals( 14, searchResult.startLine );
		assertEquals( 15, searchResult.endLine );
		assertEquals( 229, searchResult.startOffset );
		assertEquals( 251, searchResult.endOffset );
	}

	@Test
	public void checkMethodInvocation() throws Exception {
		File file = new File( "tests/files/JavaFileChecker.java" );
		JavaFileJDT javaFileChecker = new JavaFileJDT(file);
		List<SearchResult> searchResults = javaFileChecker.findMethodInvocations("Foo", null, "bar", null);

		assertNotNull(searchResults);

		assertEquals(4, searchResults.size());

		SearchResult searchResult = searchResults.get(0);

		assertNotNull(searchResult);
		assertEquals( 10, searchResult.startLine );
		assertEquals( 11, searchResult.endLine );
		assertEquals( 181, searchResult.startOffset );
		assertEquals( 200, searchResult.endOffset );
	}
	@Test
	public void checkGuessMethodInvocation() {
		File file = new File( "tests/files/JavaFileChecker.java" );
		JavaFileJDT javaFileChecker = new JavaFileJDT(file);
		List<SearchResult> results = javaFileChecker.findMethodInvocations(null, "JavaFileChecker" , "staticCall", new String[]{"String","String","String"});
		assertNotNull(results);
		assertEquals(4, results.size());
		results = javaFileChecker.findMethodInvocations("JavaFileChecker", null, "call", new String[]{"String","String","String"});
		assertNotNull(results);
		assertEquals(4, results.size());
	}
}
