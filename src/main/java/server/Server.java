package server;

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

import server.list.ClientList;
import server.list.HostList;
import server.service.HeartbeatService;
import server.service.WorkerService;

import server.Utils.Status;

public class Server {

	public static volatile Status status;

	protected final Date startTime;
	protected final String name;

	public final long exchangeInterval;
	public final int maxConnections;
	public final int port;
	public final long connectionIntervalLimit;

	public HostList hostList;
	public ClientList clientList;
	public HostInfo localHost;
	public String secret;

	private static final Logger LOG = Logger.getLogger(Server.class);

	protected ResourceTable resourceTable;

	Server(ServerConfig config) {
		LOG.info("[INFO] - started");
		this.startTime = new Date();
		this.port = config.port;
		this.name = "StandaloneServer_port_" + this.port;
		this.exchangeInterval = config.exchangeInterval;
		this.maxConnections = config.maxConnections;
		this.connectionIntervalLimit = config.connectionIntervalLimit;
		this.secret = config.secret;

		this.hostList = new HostList();
		Server.status = Status.START;
	}

	public Date getStartTime() {
		return startTime;
	}

	public String getName() {
		return name;
	}

	void start() {
		// Start worker pool and wait for connection.
		try {
			WorkerService workerService = WorkerService.getInstance();
			workerService.start(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Start sending heartbeat
		HeartbeatService heartBeatService =
				HeartbeatService.getInstance();
		heartBeatService.start(this);
		Server.status = Status.JOIN;
	}

}
