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

package com.liferay.blade.cli;

import com.liferay.blade.cli.util.Prompter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Christopher Bryan Boyd
 */
public class PrompterTest {

	@Test
	public void testConfirmDefaultQuestions() throws Exception {
		Map<String, Boolean> correctAnswerDefaultTrueTests = new HashMap<>();

		correctAnswerDefaultTrueTests.put("foobar", true);
		correctAnswerDefaultTrueTests.put("n", false);
		correctAnswerDefaultTrueTests.put("n ", false);
		correctAnswerDefaultTrueTests.put("y", true);
		correctAnswerDefaultTrueTests.put("y ", true);

		Map<String, Boolean> correctAnswerDefaultFalseTests = new HashMap<>();

		correctAnswerDefaultFalseTests.put("foobar", false);
		correctAnswerDefaultFalseTests.put("n", false);
		correctAnswerDefaultFalseTests.put("n ", false);
		correctAnswerDefaultFalseTests.put("y", true);
		correctAnswerDefaultFalseTests.put("y ", true);

		Optional<Boolean> trueDefaultAnswer = Optional.of(true);

		_testAnswers(correctAnswerDefaultTrueTests, trueDefaultAnswer);

		Optional<Boolean> falseDefaultAnswer = Optional.of(false);

		_testAnswers(correctAnswerDefaultFalseTests, falseDefaultAnswer);
	}

	@Test
	public void testConfirmQuestions() throws Exception {
		Map<String, Boolean> correctAnswerTests = new HashMap<>();

		correctAnswerTests.put(" n ", false);
		correctAnswerTests.put(" y ", true);
		correctAnswerTests.put("n", false);
		correctAnswerTests.put("N", false);
		correctAnswerTests.put("no", false);
		correctAnswerTests.put("No", false);
		correctAnswerTests.put("nO", false);
		correctAnswerTests.put("NO", false);
		correctAnswerTests.put("y", true);
		correctAnswerTests.put("Y", true);
		correctAnswerTests.put("yes", true);
		correctAnswerTests.put("YES", true);
		correctAnswerTests.put("yEs", true);
		correctAnswerTests.put("YeS", true);

		_testAnswers(correctAnswerTests);
	}

	@Test
	public void testConfirmQuestionsOutput() throws Exception {
		String answer = "y";

		String correctResult = _question + " (y/n)";

		String output = _confirm(_question, answer).getValue();

		Assert.assertEquals(output.trim(), correctResult);

		Map<String, Optional<Boolean>> correctEndingOutputTests = new HashMap<>();

		correctEndingOutputTests.put("(Y/n)", Optional.of(true));
		correctEndingOutputTests.put("(y/N)", Optional.of(false));
		correctEndingOutputTests.put("(y/n)", Optional.empty());

		for (Entry<String, Optional<Boolean>> entry : correctEndingOutputTests.entrySet()) {
			String receivedOutput = _confirm(_question, answer, entry.getValue()).getValue();

			receivedOutput = receivedOutput.trim();

			correctResult = _question + " " + entry.getKey();

			boolean assertBoolean = Objects.equals(receivedOutput, correctResult);

			Assert.assertTrue(assertBoolean);
		}
	}

	private static Entry<Boolean, String> _confirm(String question, String answerString)
		throws UnsupportedEncodingException {

		return _confirm(question, answerString, Optional.empty());
	}

	private static Entry<Boolean, String> _confirm(
			String question, String answerString, Optional<Boolean> defaultAnswer)
		throws UnsupportedEncodingException {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		PrintStream out = new PrintStream(outStream);

		InputStream in = new ByteArrayInputStream(answerString.getBytes("UTF-8"));

		boolean answer = Prompter.confirm(question, in, out, defaultAnswer);

		String outString = outStream.toString();

		return new SimpleEntry<>(answer, outString);
	}

	private void _testAnswer(String answerString, Boolean correctAnswerValue, Optional<Boolean> defaultAnswer)
		throws UnsupportedEncodingException {

		Boolean receivedAnswer = _confirm(_question, answerString, defaultAnswer).getKey();

		boolean assertBoolean = Objects.equals(receivedAnswer, correctAnswerValue);

		Assert.assertTrue(assertBoolean);
	}

	private void _testAnswers(Map<String, Boolean> correctAnswerTestMap) throws UnsupportedEncodingException {
		_testAnswers(correctAnswerTestMap, Optional.empty());
	}

	private void _testAnswers(Map<String, Boolean> correctAnswerTestMap, Optional<Boolean> defaultAnswer)
		throws UnsupportedEncodingException {

		for (Entry<String, Boolean> entry : correctAnswerTestMap.entrySet()) {
			String answerString = entry.getKey();
			Boolean correctAnswerValue = entry.getValue();

			_testAnswer(answerString, correctAnswerValue, defaultAnswer);
		}
	}

	private String _question = "Hello world?";

}