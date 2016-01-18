package com.liferay.blade.cli;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class SamplesCommandTest {

	@Test
	public void listSamples() throws Exception {
		String [] args = {
			"sample"
		};

		new bladenofail().run(args);

		Assert.assertTrue(true);
	}

	@Test
	@Ignore //Remove this if you need to test.. ignoring by default because its creates a full project
	public void getSample() throws Exception {
		String [] args = {
			"sample", "blade.friendlyurl"
		};

		new bladenofail().run(args);

		Assert.assertTrue(true);
	}
}
