package server;

import java.util.Date;

public class ServerBean {

	private final Date startTime;
	private final String name;

	protected final Server server;

	public ServerBean(Server server) {
		startTime = new Date();
		this.server = server;
		name = "StandaloneServer_port" + server.getClientPort();
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public String getName() {
		return name;
	}
	
	
}
