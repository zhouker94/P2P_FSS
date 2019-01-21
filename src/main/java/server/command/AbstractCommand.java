package server.command;

import org.json.simple.JSONObject;

import server.Server;

public abstract class AbstractCommand {

	protected Server server;

	AbstractCommand() {
	}

	public abstract JSONObject commandRun(JSONObject clientCommand);
}
