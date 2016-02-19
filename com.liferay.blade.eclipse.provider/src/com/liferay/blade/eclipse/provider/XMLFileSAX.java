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

import com.liferay.blade.api.SearchResult;
import com.liferay.blade.api.XMLFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.osgi.service.component.annotations.Component;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Andy Wu
 */
@Component(property = "file.extension=xml")
public class XMLFileSAX extends WorkspaceFile implements XMLFile {

	private File _file;
	private SAXParser _parser;

	public XMLFileSAX() {
		super();
	}

	public XMLFileSAX(File file) {
		setFile(file);
	}

	public List<SearchResult> findTag(String tagName, String value) {
		// start parsing the given file and generates results

		SearchExecutor searcher = new SearchExecutor(tagName, value);

		try {
			_parser.parse(_file, searcher);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

		return searcher.getResults();
	}
	
	@Override
	public void setFile(File file) {
		_file = file;

		SAXParserFactory factory = SAXParserFactory.newInstance();

		try {
			_parser = factory.newSAXParser();
			_parser.getXMLReader().setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		}
		catch (ParserConfigurationException | SAXException e) {
			new IllegalArgumentException(e);
		}
	}


	private class SearchExecutor extends DefaultHandler {

		public SearchExecutor(String tagName, String value) {
			_tagName = tagName;
			_value = value;
			_results = new ArrayList<>();
		}

		@Override
		public void characters(char[] ch, int start, int length)
			throws SAXException {

			String content = new String(ch, start, length);

			if (inState && _value != null && _value.equals(content)) {
				_results.add(
					new SearchResult(
						_file, 0, 0, locator.getLineNumber(),
						locator.getLineNumber(), true));
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName)
			throws SAXException {

			// reset the state when goes to end of each element

			inState = false;
		}

		public List<SearchResult> getResults() {
			return _results;
		}

		@Override
		public void setDocumentLocator(final Locator locator) {
			this.locator = locator;
		}

		@Override
		public void startDocument() throws SAXException {
			_results.clear();
		}

		@Override
		public void startElement(
				String uri, String localName, String qName,
				Attributes attributes)
			throws SAXException {

			if (_tagName.equals(qName) && _value != null) {
				inState = true;
			}
			else if (_tagName.equals(qName) && _value == null) {
				_results.add(
					new SearchResult(
						_file, 0, 0, locator.getLineNumber(),
						locator.getLineNumber(), true));
			}
		}

		private List<SearchResult> _results = null;
		private String _tagName;
		private String _value;

		// is in the target Tag

		private boolean inState = false;
		private Locator locator;

	}

}