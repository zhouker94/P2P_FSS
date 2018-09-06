package server.command;

import java.net.URI;
import java.util.LinkedList;

import org.json.simple.JSONObject;

public class PublishCommand extends AbstractCommand {

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject commandRun(JSONObject clientCommand) {

		String name = "", description = "", channel = "", owner = "";
		LinkedList<String> tags = new LinkedList<String>();
		URI uri = null;
		JSONObject resource;
		JSONObject result = new JSONObject();

		if (clientCommand.containsKey("resource")) {
			resource = (JSONObject) clientCommand.get("resource");
		}
		// if resource field is not given
		else {
			result.put("response", "error");
			result.put("errorMessage", "missing resource");
			return result;
		}
		return result;
	}

}
