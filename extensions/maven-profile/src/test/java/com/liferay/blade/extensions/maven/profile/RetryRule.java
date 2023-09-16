/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
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