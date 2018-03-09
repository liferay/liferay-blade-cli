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

package com.liferay.blade.cli.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/**
 * @author Simon Jiang
 */
public class BndProperties extends Properties {

	public void addKeyList(final String key) {
		if (_keyList.contains(key)) {
			return;
		}

		_keyList.add(key);
	}

	public void addValue(final String key, final BndPropertiesValue newBdValue) {
		BndPropertiesValue bdValue = (BndPropertiesValue)get(key);

		addKeyList(key);

		if (bdValue != null) {
			String originalValue = bdValue.getOriginalValue();

			if ((originalValue != null) && !"".equals(originalValue)) {
				StringBuilder formatedValueBuilder = new StringBuilder(bdValue.getFormatedValue());
				StringBuilder originalValueBuilder = new StringBuilder(bdValue.getOriginalValue());

				String newOriginalValue = newBdValue.getOriginalValue();

				String[] newOriginalValues = newOriginalValue.split(",");

				BndPropertiesValue inputValue = new BndPropertiesValue();

				for (String newValue : newOriginalValues) {
					if (originalValue.contains(newValue)) {
						continue;
					}

					if (bdValue.isMultiLine()) {
						formatedValueBuilder.append(",\\" + System.getProperty("line.separator"));
						formatedValueBuilder.append("\t" + newValue);
						inputValue.setFormatedValue(formatedValueBuilder.toString());
					}
					else {
						originalValueBuilder.append(",").append(newValue);
						inputValue.setFormatedValue(originalValueBuilder.toString());
					}
				}

				put(key, inputValue);
			}
			else {
				put(key, newBdValue);
			}
		}
		else {
			put(key, newBdValue);
		}
	}

	public String getReader(Reader a) throws IOException {
		StringWriter sw = new StringWriter();
		char[] buffer = new char[_PAGE_SIZE];

		int size = a.read(buffer);

		while (size > 0) {
			sw.write(buffer, 0, size);
			size = a.read(buffer);
		}

		return sw.toString();
	}

	public void load(File bndFile) throws IOException {
		try (InputStream in = new FileInputStream(bndFile)) {
			load(in);
		}
		catch (Exception e) {
		}
	}

	@Override
	public void load(InputStream inStream) throws IOException {

		// The spec says that the file must be encoded using ISO-8859-1.

		BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "ISO-8859-1"));
		String buffer;

		while ((buffer = reader.readLine()) != null) {
			String line = _convert(buffer.getBytes(), _UTF8);

			BndPropertiesValue bnd = new BndPropertiesValue();
			char c = 0;
			int pos = 0;

			// Leading whitespaces must be deleted first.

			while ((pos < line.length()) && Character.isWhitespace(c = line.charAt(pos))) {
				pos++;
			}

			// If empty line or begins with a comment character, skip this line.

			if (((line.length() - pos) == 0) || (line.charAt(pos) == '#') || (line.charAt(pos) == '!')) {
				continue;
			}

			// The characters up to the next Whitespace, ':', or '='
			// describe the key. But look for escape sequences.
			// Try to short-circuit when there is no escape char.

			int start = pos;

			boolean needsEscape = false;

			if (line.indexOf('\\', pos) != -1) {
				needsEscape = true;
			}

			StringBuilder key = needsEscape ? new StringBuilder() : null;

			while ((pos < line.length()) && !Character.isWhitespace(c = line.charAt(pos++)) && (c != '=') &&
				   (c != ':')) {

				if (needsEscape && (c == '\\')) {
					if (pos == line.length()) {

						// The line continues on the next line. If there
						// is no next line, just treat it as a key with an
						// empty value.

						line = reader.readLine();

						if (line == null) {
							line = "";
						}

						pos = 0;

						while ((pos < line.length()) && Character.isWhitespace(c = line.charAt(pos))) {
							pos++;
						}
					}
					else {
						c = line.charAt(pos++);

						switch (c) {
							case 'n':
								key.append('\n');

								break;
							case 't':
								key.append('\t');

								break;
							case 'r':
								key.append('\r');

								break;
							case 'u':
								if ((pos + 4) <= line.length()) {
									char uni = (char)Integer.parseInt(line.substring(pos, pos + 4), 16);

									key.append(uni);

									pos += 4;
								}

								break;
							default:
								key.append(c);

								break;
						}
					}
				}
				else if (needsEscape) {
					key.append(c);
				}
			}

			boolean delim = false;

			if ((c == ':') || (c == '=')) {
				delim = true;
			}

			String keyString;

			if (needsEscape) {
				keyString = key.toString();
			}
			else if (delim || Character.isWhitespace(c)) {
				keyString = line.substring(start, pos - 1);
			}
			else {
				keyString = line.substring(start, pos);
			}

			while ((pos < line.length()) && Character.isWhitespace(c = line.charAt(pos))) {
				pos++;
			}

			if (!delim && ((c == ':') || (c == '='))) {
				pos++;
				while ((pos < line.length()) && Character.isWhitespace(c = line.charAt(pos))) {
					pos++;
				}
			}

			// Short-circuit if no escape chars found.

			if (!needsEscape) {
				bnd.setOriginalValue(line.substring(pos));
				bnd.setFormatedValue(line.substring(pos));
				addKeyList(keyString);
				put(keyString, bnd);
				continue;
			}

			// Escape char found so iterate through the rest of the line.

			StringBuilder element = new StringBuilder(line.length() - pos);
			StringBuilder formatedElement = new StringBuilder(line.substring(pos));

			// formatedElement.append( line );

			while (pos < line.length()) {
				c = line.charAt(pos++);

				if (c == '\\') {
					if (pos == line.length()) {
						bnd.setMultiLine(true);
						formatedElement.append(System.getProperty("line.separator"));

						// The line continues on the next line.

						line = reader.readLine();

						formatedElement.append(line);

						// We might have seen a backslash at the end of
						// the file. The JDK ignores the backslash in
						// this case, so we follow for compatibility.

						if (line == null) {
							break;
						}

						pos = 0;

						while ((pos < line.length()) && Character.isWhitespace(c = line.charAt(pos))) {
							pos++;
						}

						element.ensureCapacity(line.length() - pos + element.length());
					}
					else {
						c = line.charAt(pos++);

						switch (c) {
							case 'n':
								element.append('\n');
								formatedElement.append('\n');

								break;
							case 't':
								element.append('\t');
								formatedElement.append('\t');

								break;
							case 'r':
								element.append('\r');
								formatedElement.append('\r');

								break;
							case 'u':
								if ((pos + 4) <= line.length()) {
									char uni = (char)Integer.parseInt(line.substring(pos, pos + 4), 16);

									element.append(uni);

									pos += 4;
								}

								break;
							default:
								element.append(c);

								break;
						}
					}
				}
				else {
					element.append(c);
				}
			}

			bnd.setOriginalValue(element.toString());
			bnd.setFormatedValue(formatedElement.toString());
			addKeyList(keyString);
			put(keyString, bnd);
		}
	}

	@Override
	public void store(OutputStream out, String header) throws IOException {
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));

		if (header != null) {
			writer.println("#" + header);

			Calendar calender = Calendar.getInstance();

			writer.println("#" + calender.getTime());
		}

		// Reuse the same buffer	.

		StringBuilder s = new StringBuilder();

		for (String keyString : _keyList) {
			_formatForOutput(keyString, s, true);
			s.append(": ");

			Object value = get(keyString);

			if (value instanceof BndPropertiesValue) {
				final BndPropertiesValue bndValue = (BndPropertiesValue)value;

				writer.println(s.append(bndValue.getFormatedValue()));
			}
			else {
				_formatForOutput((String)value, s, false);
				writer.println(s);
			}
		}

		writer.flush();
	}

	private String _convert(byte[] buffer, Charset charset) throws IOException {
		CharsetDecoder decoder = charset.newDecoder();
		ByteBuffer bb = ByteBuffer.wrap(buffer);
		CharBuffer cb = CharBuffer.allocate(buffer.length * 4);

		CoderResult result = decoder.decode(bb, cb, true);

		if (!result.isError()) {
			return new String(cb.array(), 0, cb.position());
		}

		throw new CharacterCodingException();
	}

	private void _formatForOutput(String str, StringBuilder buffer, boolean key) {
		if (key) {
			buffer.setLength(0);
			buffer.ensureCapacity(str.length());
		}
		else {
			buffer.ensureCapacity(buffer.length() + str.length());
		}

		boolean head = true;
		int size = str.length();

		for (int i = 0; i < size; i++) {
			char c = str.charAt(i);

			switch (c) {
				case '\n':
					buffer.append("\\n");

					break;
				case '\r':
					buffer.append("\\r");

					break;
				case '\t':
					buffer.append("\\t");

					break;
				case ' ':
					buffer.append(head ? "\\ " : " ");

					break;

					// case '\\':

				case '!':
				case '#':

					// case '=':
					// case ':':

					buffer.append('\\');
					buffer.append(c);

					break;
				default:
					if ((c < ' ') || (c > '~')) {
						String hex = Integer.toHexString(c);

						buffer.append("\\u0000".substring(0, 6 - hex.length()));
						buffer.append(hex);
					}
					else {
						buffer.append(c);
					}
			}

			if (c != ' ') {
				head = key;
			}
		}
	}

	private static final int _PAGE_SIZE = 4096;

	private static final Charset _UTF8 = Charset.forName("UTF-8");

	private static final List<String> _keyList = new ArrayList<>();
	private static final long serialVersionUID = 1L;

}