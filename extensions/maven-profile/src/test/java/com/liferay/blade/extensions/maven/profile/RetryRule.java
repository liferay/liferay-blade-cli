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

package com.liferay.blade.extensions.maven.profile;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Lawrence Lee
 */
public class RetryRule implements TestRule {

	public RetryRule(int retryCount) {
		_retryCount = retryCount;
	}

	@Override
	public Statement apply(Statement base, Description description) {
		return _statement(base, description);
	}

	private Statement _statement(final Statement base, final Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				Throwable caughtThrowable = null;

				for (int i = 0; i < _retryCount; i++) {
					try {
						base.evaluate();

						return;
					}
					catch (Throwable t) {
						caughtThrowable = t;

						System.err.println(description.getDisplayName() + ": run " + (i + 1) + " failed.");
					}
				}

				System.err.println(description.getDisplayName() + ": giving up after " + _retryCount + " failures.");

				throw caughtThrowable;
			}

		};
	}

	private int _retryCount;

}