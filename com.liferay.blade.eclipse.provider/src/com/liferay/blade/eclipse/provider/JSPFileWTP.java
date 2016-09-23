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

import com.liferay.blade.api.JSPFile;
import com.liferay.blade.api.JavaFile;
import com.liferay.blade.api.SearchResult;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslator;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.osgi.service.component.annotations.Component;
import org.w3c.dom.NodeList;

@Component(
	property = {
		"file.extension=jsp"
	},
	service = {
		JavaFile.class,
		JSPFile.class
	}
)
public class JSPFileWTP extends JavaFileJDT implements JSPFile {

	public JSPFileWTP() {
	}

	public JSPFileWTP(File file) {
		super(file);

		getTranslation(file);
	}

	private JSPTranslationPrime createJSPTranslation() {
		IDOMModel jspModel = null;

		try {
			// try to find the file in the current workspace, if it can't find it then fall back to copy

			final IFile jspFile = getIFile(getFile());

			jspModel = (IDOMModel) StructuredModelManager.getModelManager()
					.getModelForRead(jspFile);
			final IDOMDocument domDocument = jspModel.getDocument();
			final IDOMNode domNode = (IDOMNode) domDocument
					.getDocumentElement();

			final IProgressMonitor npm = new NullProgressMonitor();
			final JSPTranslator translator = new JSPTranslatorPrime();

			if (domNode != null) {
				translator.reset((IDOMNode) domDocument.getDocumentElement(),
						npm);
			} else {
				translator.reset((IDOMNode) domDocument.getFirstChild(), npm);
			}

			translator.translate();

			final IJavaProject javaProject = JavaCore
					.create(jspFile.getProject());

			_translation = new JSPTranslationPrime(javaProject, translator,
					jspFile);

			return _translation;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jspModel != null) {
				jspModel.releaseFromRead();
			}
		}

		return null;
	}

	@Override
	protected SearchResult createSearchResult(String searchContext, int startOffset, int endOffset,
			int startLine, int endLine, boolean fullMatch) {

		IDOMModel jspModel = null;

		try {
			final JSPTranslationPrime translation = getTranslation(getFile());

			final int jspStartOffset = translation.getJspOffset(startOffset);
			final int jspEndOffset = translation.getJspOffset(endOffset);

			jspModel = (IDOMModel) StructuredModelManager.getModelManager()
					.getModelForRead(translation._jspFile);
			final IDOMDocument domDocument = jspModel.getDocument();

			final IStructuredDocument structuredDocument = domDocument
					.getStructuredDocument();
			final int jspStartLine = structuredDocument
					.getLineOfOffset(jspStartOffset) + 1;
			final int jspEndLine = structuredDocument
					.getLineOfOffset(jspEndOffset) + 1;

			return super.createSearchResult(searchContext, jspStartOffset, jspEndOffset,
					jspStartLine, jspEndLine, fullMatch);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jspModel != null) {
				jspModel.releaseFromRead();
			}
		}

		return super.createSearchResult(searchContext, startOffset, endOffset, startLine,
				endLine, fullMatch);
	}

	@Override
	protected char[] getJavaSource() {
		JSPTranslation translation = getTranslation(getFile());

		return translation.getJavaText().toCharArray();
	}

	private JSPTranslationPrime getTranslation(File file) {
		try {
			synchronized (_map) {
				WeakReference<JSPTranslationPrime> translationRef = _map.get(file);

				if (translationRef == null || translationRef.get() == null) {
					final JSPTranslationPrime newTranslation = createJSPTranslation();

					_map.put(file, new WeakReference<JSPTranslationPrime>(newTranslation));

					_translation = newTranslation;
				}
				else {
					_translation = translationRef.get();
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}

		return _translation;
	}

	@Override
	public List<SearchResult> findJSPTags(String tagName , String[] attrNames , String[] attrValues) {

		if (tagName == null || tagName.isEmpty()) {
			throw new IllegalArgumentException("tagName can not be null or empty");
		}

		if ((attrNames != null && attrValues != null) && attrNames.length != attrValues.length) {
			throw new IllegalArgumentException("If attrValues is specified it must match the attrNames array in lengh");
		}

		final List<SearchResult> searchResults = new ArrayList<>();

		final IFile jspFile = getTranslation(getFile())._jspFile;

		IDOMModel jspModel = null;

		try {
			jspModel = (IDOMModel) StructuredModelManager
				.getModelManager().getModelForRead(jspFile);
			final IDOMDocument domDocument = jspModel.getDocument();

			final IStructuredDocument structuredDocument = domDocument
					.getStructuredDocument();

			final NodeList nodeList = domDocument.getElementsByTagName(tagName);

			for (int i = 0; i < nodeList.getLength(); i++) {
				final IDOMNode domNode = (IDOMNode) nodeList.item(i);

				if (attrNames == null) {
					int startOffset = domNode.getStartOffset();
					int endOffset = domNode.getEndOffset();
					int jspStartLine = structuredDocument.getLineOfOffset(startOffset) + 1;
					int jspEndLine = structuredDocument.getLineOfOffset(endOffset) + 1;
					searchResults.add(super.createSearchResult(null,
							startOffset,endOffset, jspStartLine,jspEndLine, true));

				} else {
					for (int j = 0; j < attrNames.length; j++) {
						final IDOMNode attrNode = (IDOMNode) domNode
								.getAttributes().getNamedItem(attrNames[j]);

						if (attrNode != null) {
							if (attrValues != null && !(attrValues[j].equals(attrNode.getNodeValue()))) {
								continue;
							}

							int startOffset = attrNode.getStartOffset();
							int endOffset = attrNode.getEndOffset();
							int jspStartLine = structuredDocument.getLineOfOffset(startOffset) + 1;
							int jspEndLine = structuredDocument.getLineOfOffset(endOffset) + 1;

							searchResults.add(super.createSearchResult(null,
									startOffset, endOffset, jspStartLine,jspEndLine, true));
						}
					}
				}
			}
		} catch (IOException | CoreException e) {
			e.printStackTrace();
		} finally {
			if (jspModel != null) {
				jspModel.releaseFromRead();
			}
		}

		return searchResults;
	}

	/**
	 * A simple subclass to hold a reference to the original jspFile
	 */
	private class JSPTranslationPrime extends JSPTranslation {

		private IFile _jspFile;

		public JSPTranslationPrime(IJavaProject javaProject,
				JSPTranslator translator, IFile jspFile) {
			super(javaProject, translator);

			_jspFile = jspFile;
		}
	}

	private class JSPTranslatorPrime extends JSPTranslator {
		@Override
		protected void handleIncludeFile(String filename) {
			try {
				super.handleIncludeFile(filename);
			} catch (Exception e) {
				// suppress errors in handling include files
			}
		}
	}

	private static Map<File, WeakReference<JSPTranslationPrime>> _map = new WeakHashMap<>();

	private JSPTranslationPrime _translation;
}
