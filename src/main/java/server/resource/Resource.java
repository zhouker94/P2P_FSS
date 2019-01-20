package server.resource;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import server.HostInfo;

public class Resource {
	
	private String name;
	private String description;
	private LinkedList<String> tags;
	private HostInfo ezserver;

	public ResourceKey rKey;

	public Resource(String name, String description, LinkedList<String> tags, URI uri, String channel, String owner,
			HostInfo ezserver) {
		this.name = name;
		this.description = description;
		this.tags = tags;
		this.ezserver = ezserver;
		this.rKey = new ResourceKey(owner, channel, uri);
	}

	public Resource() {
		// all members set to be empty
		this("", "", new LinkedList<String>(), null, "", "", null);
	}

	public String toString() {
		return "Resource [name=" + name + ", description=" + description + ", tags=" + tags + ", uri=" + rKey.getUri()
				+ ", channel=" + rKey.getChannel() + ", owner=" + rKey.getOwner() + ",ezserver=" + ezserver + "]";
	}

	/**
	 * @return the JSON of resource
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject resource_json = new JSONObject();
		resource_json.put("name", this.name);

		JSONArray tags_json = new JSONArray();
		Iterator<String> it = tags.iterator();
		while (it.hasNext()) {
			tags_json.add(it.next());
		}

		resource_json.put("tags", tags_json);
		resource_json.put("description", this.description);
		resource_json.put("uri", this.rKey.getUri().toString());
		resource_json.put("channel", this.rKey.getChannel());
		resource_json.put("owner", this.rKey.getOwner());
		resource_json.put("ezserver", this.ezserver.toString());
		return resource_json;
	}
}
