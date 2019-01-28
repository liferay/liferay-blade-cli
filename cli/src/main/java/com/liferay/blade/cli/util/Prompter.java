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

import java.io.InputStream;
import java.io.PrintStream;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

import org.apache.commons.io.input.CloseShieldInputStream;

/**
 * @author Christopher Bryan Boyd
 */
public class Prompter {

	public static boolean confirm(String question) {
		return confirm(question, System.in, System.out, Optional.empty());
	}

	public static boolean confirm(String question, boolean defaultAnswer) {
		return confirm(question, System.in, System.out, Optional.of(defaultAnswer));
	}

	public static boolean confirm(String question, InputStream in, PrintStream out, Optional<Boolean> defaultAnswer) {
		String questionWithPrompt = _buildQuestionWithPrompt(question, defaultAnswer);

		Optional<Boolean> answer = _getBooleanAnswer(questionWithPrompt, in, out, defaultAnswer);

		if (answer.isPresent()) {
			return answer.get();
		}
		else {
			throw new NoSuchElementException("Unable to acquire an answer");
		}
	}

	private static String _buildQuestionWithPrompt(String question, Optional<Boolean> defaultAnswer) {
		String yesDefault = "y";
		String noDefault = "n";

		if (defaultAnswer.isPresent()) {
			if (defaultAnswer.get()) {
				yesDefault = "Y";
			}
			else {
				noDefault = "N";
			}
		}

		return question + " (" + yesDefault + "/" + noDefault + ")";
	}

	private static Optional<Boolean> _getBooleanAnswer(
		String questionWithPrompt, InputStream inputStream, PrintStream printStream, Optional<Boolean> defaultAnswer) {

		Optional<Boolean> answer = null;

		try (CloseShieldInputStream closeShieldInputStream = new CloseShieldInputStream(inputStream);
			Scanner scanner = new Scanner(closeShieldInputStream)) {

			while ((answer == null) || !answer.isPresent()) {
				printStream.println(questionWithPrompt);

				String readLine = null;

				while (((answer == null) || !answer.isPresent()) && !Objects.equals(answer, defaultAnswer) &&
					   scanner.hasNextLine()) {

					readLine = scanner.nextLine();

					if (readLine != null) {
						readLine = readLine.toLowerCase();

						switch (readLine.trim()) {
							case "y":
							case "yes":
								answer = Optional.of(true);

								break;
							case "n":
							case "no":
								answer = Optional.of(false);

								break;
							default:
								if (defaultAnswer.isPresent()) {
									answer = defaultAnswer;
								}
								else {
									printStream.println("Unrecognized input: " + readLine);

									continue;
								}

								break;
						}
					}
				}
				else {
					answer = defaultAnswer;
				}
			}
		}
		catch (IllegalStateException ise) {
			throw new RuntimeException(ise);
		}
		catch (Exception exception) {
			if (defaultAnswer.isPresent()) {
				answer = defaultAnswer;
			}
		}

		return answer;
	}

}