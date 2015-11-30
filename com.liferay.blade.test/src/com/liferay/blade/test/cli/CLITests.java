package com.liferay.blade.test.cli;

import static org.junit.Assert.assertEquals;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.CommandException;
import com.liferay.blade.cli.blade;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.apache.felix.service.command.CommandProcessor;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class CLITests {

	static class TestCommand implements Command {
		private String value;

		public TestCommand() {
		}

		@Override
		public Object execute(Map<String, ?> parameters) throws CommandException {
			return null;
		}

		@Override
		public Object execute(String... args) throws CommandException {
			value = args[0];
			return null;
		}

		String getValue() {
			return value;
		}
	}

	@Test
	public void testCommand() throws Exception {
		TestCommand testCmd = new TestCommand();

		BundleContext context = FrameworkUtil.getBundle(CLITests.class).getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<String, Object>();

		properties.put(CommandProcessor.COMMAND_FUNCTION, "testCommand");

		context.registerService(Command.class, testCmd, properties);

		new blade().run(new String[] { "testCommand", "testValue", });

		assertEquals("testValue", testCmd.getValue());
	}

}
