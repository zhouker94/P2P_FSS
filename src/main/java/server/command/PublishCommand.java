package server.command;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import server.HostInfo;
import server.resource.Resource;

import server.event.SubscribeListenerManager;

public class PublishCommand extends AbstractCommand {

	HostInfo host;
	private SubscribeListenerManager slm;

	public PublishCommand(){

	}

	PublishCommand(SubscribeListenerManager slm, HostInfo host){
		this.host = host;
		this.slm = slm;
	}

	@SuppressWarnings("unchecked")
	@Override
	JSONObject parse(JSONObject clientCommand) {
		String name = "", description = "", channel = "", owner = "";
		LinkedList<String> tags = new LinkedList<>();
		URI uri;
		JSONObject resource;
		JSONObject result = new JSONObject();

		if (clientCommand.containsKey("resource")) {
			resource = (JSONObject) clientCommand.get("resource");
		}
		else {
			result.put("response", "error");
			result.put("errorMessage", "missing resource");
			return result;
		}

		if (resource.containsKey("name")) {
			name = resource.get("name").toString();
		}

		if (resource.containsKey("tags")) {
			JSONArray tag_array = (JSONArray) resource.get("tags");
			for (Object o : tag_array) {
				String tag = o.toString();
				tags.add(tag);
			}
		}

		if (resource.containsKey("description")) {
			description = resource.get("description").toString();
		}

		if (resource.containsKey("channel")) {
			channel = resource.get("channel").toString();
		}

		if (resource.containsKey("owner")) {
			owner = resource.get("owner").toString();
			if (owner.equals("*")) {
				result.put("response", "error");
				result.put("errorMessage", "invalid resource");
				return result;
			}
		}

		try {
			if (resource.containsKey("uri")) {
				uri = new URI(resource.get("uri").toString());
				String scheme = uri.getScheme();
				if ("file".equals(scheme)) {
					result.put("response", "error");
					result.put("errorMessage", "cannot publish resource");
					return result;
				}
			} else {
				result.put("response", "error");
				result.put("errorMessage", "cannot publish resource");
				return result;
			}
		} catch (URISyntaxException e) {
			result.put("response", "error");
			result.put("errorMessage", "cannot publish resource");
			return result;
		}

		Resource rs = new Resource(name, description, tags, uri, channel, owner, host);
		return rs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject commandRun(JSONObject clientCommand) {
		JSONObject result = parse(clientCommand);
		/** prepare command and arguments */
		Object[] args = new Object[2];
		args[0] = key;
		args[1] = rs;
		accessResources("ADD", args);
		result.put("response", "success");

		// inform all subscribers of the resource published just now
		slm.informSubscribersOfNewResource(rs);
		listenerManager_secure.informSubscribersOfNewResource(rs);
		return result;
	}

}
