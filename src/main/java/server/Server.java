package server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import server.resource.Resource;
import server.resource.ResourceKey;
import server.service.WorkerService;
import server.util.*;

public class Server {
	
	protected final Date startTime;
	protected final String name;
	
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
		name = "StandaloneServer_port" + config.port;
	}

	public Date getStartTime() {
		return startTime;
	}

	public String getName() {
		return name;
	}
	
	public static void run() {

		// Start Worker Service and wait for connection.
		try {
			System.out.println("Now waiting for connection from client...");
			WorkerService.getInstance().startService();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Start a new thread to exchange information
		new Thread(() -> ServerUtils.sendHostlist(ServerConfig.exchangeInterval * 1000, ServerConfig.local_host,
				ServerConfig.host_list)).start();
	}

}
