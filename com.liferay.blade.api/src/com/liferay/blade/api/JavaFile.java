package com.liferay.blade.api;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface JavaFile extends SourceFile {

	List<SearchResult> findCatchExceptions(String[] exceptions);

	List<SearchResult> findImplementsInterface(String interfaceName);

	SearchResult findImport(String importName);

	List<SearchResult> findMethodDeclaration(String name, String[] params);

	List<SearchResult> findMethodInvocations(String typeHint, String expressionValue, String methodName, String[] methodParamTypes);

	SearchResult findPackage(String packageName);

	List<SearchResult> findServiceAPIs(String[] serviceApiPrefixes);

	List<SearchResult> findSuperClass(String superClassName);

}
