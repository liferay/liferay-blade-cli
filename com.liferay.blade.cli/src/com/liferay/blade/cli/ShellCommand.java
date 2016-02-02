package com.liferay.blade.cli;

import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;

import aQute.remote.api.Agent;
import aQute.remote.api.Event;
import aQute.remote.api.Supervisor;
import aQute.remote.util.AgentSupervisor;

import java.io.InputStream;

import java.net.InetSocketAddress;
import java.net.Socket;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Gregory Amerson
 */
public class ShellCommand {

	public static boolean canConnect(String host, int port) {
		InetSocketAddress address = new InetSocketAddress(
			host, Integer.valueOf(port));
		InetSocketAddress local = new InetSocketAddress(0);

		InputStream in = null;

		try (Socket socket = new Socket()) {
			socket.bind(local);
			socket.connect(address, 3000);
			in = socket.getInputStream();

			return true;
		}
		catch (Exception e) {
		}

		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) {
				}
			}
		}

		return false;
	}

	public ShellCommand(blade blade, ShellOptions options) throws Exception {
		_blade = blade;
		_options = options;
		_port = options.port() != 0 ? options.port() : Agent.DEFAULT_PORT;
	}

	public void execute() throws Exception {
		if (!canConnect("localhost", _port)) {
			addError(
				"sh",
				"Unable to connect to remote agent on port " + _port + ". " +
					"To install the agent bundle run the command \"blade " +
						"agent install\".");
			return;
		}

		String gogoCommand = StringUtils.join(_options._arguments(), " ");

		executeCommand(gogoCommand);
	}

	@Arguments(arg = {"gogo-command", "args..."})
	public interface ShellOptions extends Options {

		@Description("The port to use to connect to remote agent")
		public int port();

	}

	public class ShellSupervisor
		extends AgentSupervisor<Supervisor, Agent>implements Supervisor {

		public ShellSupervisor(blade blade) {
			_blade = blade;
		}

		public void connect(String host, int port) throws Exception {
			super.connect(Agent.class, this, host, port);
		}

		@Override
		public void event(Event e) throws Exception {
		}

		@Override
		public boolean stderr(String out) throws Exception {
			_blade.err().print(out);
			return true;
		}

		@Override
		public boolean stdout(String out) throws Exception {
			_blade.out().print(out.replaceAll(".*>.*$", ""));
			return true;
		}

		private final blade _blade;

	}

	private void addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private void executeCommand(String cmd) throws Exception {
		ShellSupervisor supervisor = new ShellSupervisor(_blade);

		supervisor.connect("localhost", _port);

		if (!supervisor.getAgent().redirect(-1)) {
			addError("sh", "Unable to redirect input to agent.");
			return;
		}

		supervisor.getAgent().stdin(cmd);
		supervisor.close();
	}

	private final blade _blade;
	private final ShellOptions _options;
	private final int _port;

}