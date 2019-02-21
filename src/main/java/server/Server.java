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

	public final long exchangeInterval;
	public final int maxConnections;
	public final long connectionIntervalLimit;

	public HostList hostList;
	public ClientList clientList;
	public HostInfo localHost;

	private static final Logger LOG = Logger.getLogger(Server.class);

	protected ResourceTable resourceTable;

	Server(ServerConfig config) {
		LOG.info("[INFO] - started");
		this.startTime = new Date();

		this.exchangeInterval = config.exchangeInterval;
		this.maxConnections = config.maxConnections;
		this.connectionIntervalLimit = config.connectionIntervalLimit;

		this.hostList = new HostList();

		String server_name = "StandaloneServer_port_" + config.port;
		this.localHost = new HostInfo(
		        server_name, config.port, config.secret);
		Server.status = Status.START;
	}

	public Date getStartTime() {
		return startTime;
	}

	public String getName() {
		return this.localHost.getHostname();
	}

	void start() throws ServerException{

		// Start worker pool and wait for connection.
		try {
			WorkerService workerService = WorkerService.getInstance();
			workerService.start(this);
		} catch (IOException e) {
			throw new ServerException(e.toString(), e);
		}

		// Start sending heartbeat
		HeartbeatService heartBeatService =
				HeartbeatService.getInstance();
		heartBeatService.start(this);
		Server.status = Status.JOIN;
	}

	class ServerException extends Exception{
		ServerException(String msg, Throwable t) {
			super(msg, t);
		}
	}
}
