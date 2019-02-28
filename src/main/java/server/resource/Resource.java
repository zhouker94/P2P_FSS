package server.resource;

import java.net.URI;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import server.HostInfo;

public class Resource {
	
	private String name;
	private String description;
	private LinkedList<String> tags;
	private HostInfo ezserver;
	private ResourceKey resource_key;

	public Resource(
			String name,
			String description,
			LinkedList<String> tags,
			URI uri,
			String channel,
			String owner,
			HostInfo ezserver) {

		this.name = name;
		this.description = description;
		this.tags = tags;
		this.ezserver = ezserver;
		this.resource_key = new ResourceKey(owner, channel, uri);
	}

	public Resource() {
		// all members set to be empty
		this("", "", new LinkedList<>(), null, "", "", null);
	}

	public String toString() {
		return "Resource [name=" + name + ", description=" + description + ", tags=" + tags + ", uri=" + resource_key.getUri()
				+ ", channel=" + resource_key.getChannel() + ", owner=" + resource_key.getOwner() + ",ezserver=" + ezserver + "]";
	}

	/**
	 * @return the JSON of resource
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject resource_json = new JSONObject();
		resource_json.put("name", this.name);

		JSONArray tags_json = new JSONArray();
		tags_json.addAll(tags);
		resource_json.put("tags", tags_json);

		resource_json.put("description", this.description);
		resource_json.put("uri", this.resource_key.getUri().toString());
		resource_json.put("channel", this.resource_key.getChannel());
		resource_json.put("owner", this.resource_key.getOwner());
		resource_json.put("ezserver", this.ezserver.toString());
		return resource_json;
	}

	@Override
	public boolean equals(Object rhs) {
		if (rhs instanceof Resource) {
			return this.resource_key.equals(((Resource) rhs).resource_key);
		}
		return false;
	}

	public class ResourceKey {

		private String owner;
		private String channel;
		private URI uri;

		ResourceKey(String owner, String channel, URI uri) {
			this.owner = owner;
			this.channel = channel;
			this.uri = uri;
		}

		String getOwner() {
			return owner;
		}

		public String getChannel() {
			return channel;
		}

		public URI getUri() {
			return uri;
		}

		@Override
		public int hashCode() {
			String str = owner + channel + uri.toString();
			return str.hashCode();
		}

		@Override
		public boolean equals(Object rhs) {
			if (rhs instanceof ResourceKey) {
				ResourceKey rhs_key = (ResourceKey) rhs;
				return this.owner.equals(rhs_key.owner) && this.channel.equals(rhs_key.owner)
						&& this.uri.equals(rhs_key.uri);
			}
			return false;
		}
	}

}
