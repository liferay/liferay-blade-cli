package com.liferay.blade.cli;

import aQute.remote.api.Agent;
import aQute.remote.api.Event;
import aQute.remote.api.Supervisor;
import aQute.remote.util.AgentSupervisor;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

public class ShellCommand {

	final private blade _blade;
	final private ShellOptions _options;

	public ShellCommand(blade blade, ShellOptions options) throws Exception {
		_blade = blade;
		_options = options;

		execute();
	}

	private void addError(String prefix, String msg) {
		_blade.addErrors(prefix, Collections.singleton(msg));
	}

	private void execute() throws Exception {
		if(!canConnect("localhost", Agent.DEFAULT_PORT)) {
			addError("sh", "Unable to connect to remote agent. Install agent bundle?");
			return;
		}

		ShellSupervisor supervisor = new ShellSupervisor(_blade);

		supervisor.connect("localhost", Agent.DEFAULT_PORT);

		if (!supervisor.getAgent().redirect(-1)) {
			addError("sh", "Unable to redirect input to agent.");
			return;
		}

		String gogoCommand = StringUtils.join(_options._arguments(), " ");

		supervisor.getAgent().stdin(gogoCommand);

		supervisor.close();
	}

	private boolean canConnect(String host, int port) {
        InetSocketAddress address = new InetSocketAddress(host, Integer.valueOf( port ));
        InetSocketAddress local = new InetSocketAddress(0);

        InputStream in = null;

		try (Socket socket = new Socket()) {
            socket.bind(local);
            socket.connect(address, 3000);
            in = socket.getInputStream();

            return true;
        }
        catch( Exception e ) {
        }
        finally
        {
            if( in != null ) {
                try {
                    in.close();
                }
                catch( Exception e ) {
                }
            }
        }

		return false;
	}

	public class ShellSupervisor extends AgentSupervisor<Supervisor, Agent>implements Supervisor
	{
		private final blade _blade;

		public ShellSupervisor(blade blade) {
			super();
			_blade = blade;
		}

	    @Override
	    public boolean stdout(String out) throws Exception {
	    	_blade.out().print(out.replaceAll(".*>.*$", ""));
	        return true;
	    }

	    @Override
	    public boolean stderr(String out) throws Exception {
	    	_blade.err().print(out);
	        return true;
	    }

	    public void connect(String host, int port) throws Exception {
	        super.connect(Agent.class, this, host, port);
	    }

	    @Override
	    public void event(Event e) throws Exception {
	    }

	}
}