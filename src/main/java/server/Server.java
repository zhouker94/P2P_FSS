package server;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import server.hostlist.HostInfo;
import server.service.HeartbeatService;
import server.service.WorkerService;

import server.ServerUtils.Status;

public class Server {

	public static volatile Status status;

	protected final Date startTime;
	protected final int port;
	protected final String name;
	protected final long exchangeInterval;
	protected final HostInfo local_host;
	protected List<HostInfo> host_list;
	protected final int maxConnections;

	private static final Logger LOG = Logger.getLogger(Server.class);

	protected ResourceTable resourceTable;

	Server(ServerConfig config) {
		LOG.info("[INFO] - started");
		this.startTime = new Date();
		this.port = config.port;
		this.name = "StandaloneServer_port_" + this.port;
		this.exchangeInterval = config.exchangeInterval;
		this.local_host = config.local_host;
		this.host_list = config.host_list;
		this.maxConnections = config.maxConnections;

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
			workerService.start(this.port, this.maxConnections);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Start sending heartbeat
		try {
			HeartbeatService heartBeatService =
					HeartbeatService.getInstance();
			heartBeatService.start(this.exchangeInterval, this.maxConnections);
		} catch () {

		}
		Server.status = Status.JOIN;
	}

}
