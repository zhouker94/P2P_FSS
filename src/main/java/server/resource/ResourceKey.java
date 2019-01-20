package server.resource;

import java.net.URI;

public class ResourceKey {
	private String owner;
	private String channel;
	private URI uri;

	public ResourceKey(String owner, String channel, URI uri) {
		this.owner = owner;
		this.channel = channel;
		this.uri = uri;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	@Override
	public int hashCode() {
		String str = owner + channel + uri.toString();
		return str.hashCode();
	}

	@Override
	public boolean equals(Object rhs) {

		if (rhs instanceof ResourceKey) {
			ResourceKey rhs_key;
			rhs_key = (ResourceKey) rhs;
			if (this.owner.equals(rhs_key.owner) && this.channel.equals(rhs_key.owner)
					&& this.uri.toString().equals(rhs_key.uri.toString()))
				return true;
		}
		return false;
	}
}
