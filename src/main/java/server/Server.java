package server;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import server.service.WorkerService;
import server.util.*;

public class Server {

	protected final Date startTime;
	protected final String name;
	protected final long exchangeInterval;
	protected final HostEntity local_host;
	protected List<HostEntity> host_list;
	protected final int maxConnections;

	protected static final Logger LOG;
	protected volatile State state = State.INITIAL;
	protected RequestProcessor processor;

	protected ResourceTable resourceTable;

	static {
		LOG = Logger.getLogger(Server.class);
	}

	protected enum State {
		INITIAL, RUNNING, ERROR
	}

	public Server(ServerConfig config) {
		LOG.info("[INFO] - started");
		startTime = new Date();
		name = "StandaloneServer_port_" + config.port;
		exchangeInterval = config.exchangeInterval;
		local_host = config.local_host;
		host_list = config.host_list;
		maxConnections = config.maxConnections;
	}

	public Date getStartTime() {
		return startTime;
	}

	public String getName() {
		return name;
	}

	public void start() {
		// Start Worker Service and wait for connection.
		try {
			System.out.println("Now waiting for connection from client...");
			WorkerService workerService = WorkerService.getInstance();
			workerService.startService(this.maxConnections);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Start a new thread to exchange information
		new Thread(() -> ServerUtils.sendHostlist(this.exchangeInterval * 1000, local_host, host_list))
				.start();
	}

}
