package server.command;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import server.Utils;

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
		// if resource field are not given
		else {
			result.put("response", "error");
			result.put("errorMessage", "missing resource");
			return result;
		}

		if (resource.containsKey("name")) {
			name = resource.get("name").toString();
			name = Utils.clean(name);
		}
		/** get tags from command */
		if (resource.containsKey("tags")) {
			JSONArray tag_array = (JSONArray) resource.get("tags");
			for (int i = 0; i < tag_array.size(); i++) {
				String tag = tag_array.get(i).toString();
				tag = Utils.clean(tag);
				tags.add(tag);
			}
		}

		if (resource.containsKey("description")) {
			description = resource.get("description").toString();
			description = Utils.clean(description);
		}

		if (resource.containsKey("channel")) {
			channel = resource.get("channel").toString();
			channel = Utils.clean(channel);
		}

		if (resource.containsKey("owner")) {
			owner = resource.get("owner").toString();
			owner = Utils.clean(owner);
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
		/** end of getting all the fields from command */

		result = new JSONObject();
		Resource rs = new Resource(name, description, tags, uri, channel, owner, host);
		ResourceKey key = new ResourceKey(owner, channel, uri);
		/** prepare command and arguments */
		Object[] args = new Object[2];
		args[0] = key;
		args[1] = rs;
		accessResources("ADD", args);
		result.put("response", "success");

		// inform all subscribers of the resource published just now
		listenerManager.informSubscribersOfNewResource(rs);
		listenerManager_secure.informSubscribersOfNewResource(rs);
		return result;
	}
}
