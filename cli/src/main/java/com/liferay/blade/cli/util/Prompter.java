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
import java.util.Optional;
import java.util.Scanner;

/**
 * @author Christopher Bryan Boyd
 */
public class Prompter {

	public static boolean askBoolean(String question) {
		return askBoolean(question, System.in, System.out, Optional.empty());
	}

	public static boolean askBoolean(String question, boolean defaultAnswer) {
		return askBoolean(question, System.in, System.out, Optional.of(defaultAnswer));
	}

	public static boolean askBoolean(String question, InputStream in, PrintStream out) {
		return askBoolean(question, in, out, Optional.empty());
	}

	public static boolean askBoolean(String question, InputStream in, PrintStream out, boolean defaultAnswer) {
		return askBoolean(question, in, out, Optional.of(defaultAnswer));
	}

	public static boolean askBoolean(
		String question, InputStream in, PrintStream out, Optional<Boolean> defaultAnswer) {

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

		return question + " [" + yesDefault + "/" + noDefault + "]";
	}

	private static Optional<Boolean> _getBooleanAnswer(
		String questionWithPrompt, InputStream in, PrintStream out, Optional<Boolean> defaultAnswer) {

		Optional<Boolean> answer = Optional.empty();

		while (!answer.isPresent()) {
			out.println(questionWithPrompt);

			try (Scanner scanner = new Scanner(in)) {
				String decision = scanner.nextLine();

				decision = decision.toLowerCase();

				decision = decision.trim();

				switch (decision) {
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
							answer = Optional.of(defaultAnswer.get());
						}
						else {
							out.println("Unrecognized input: " + decision);
							continue;
						}

						break;
				}
			}
			catch (NoSuchElementException nsee) {
				if (defaultAnswer.isPresent()) {
					answer = Optional.of(defaultAnswer.get());
				}
				else {
					out.println(nsee.getMessage());
					continue;
				}
			}
		}

		return answer;
	}

}