package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

import resource.Resource;
import resource.ResourceKey;
import server.service.WorkerService;
import server.util.*;

public class Server {

	// Define the port number, '33254' by default
	public static int port = 33254;

	private static ServerSocket server_socket;

	// Hash map of resources
	private static HashMap<ResourceKey, Resource> resource_map = new HashMap<ResourceKey, Resource>();
	// Max connections
	public static int numConnections = 100;
	// A list of Server Records
	private static ArrayList<HostInfo> host_list = new ArrayList<HostInfo>();
	// store those clients' addresses who just used the server
	public static ArrayList<InetAddress> client_list = new ArrayList<InetAddress>();
	// Server's secret
	private static String secret = "";
	// advertised host name
	private static String advertisedHostName = "";
	// connection interval limit
	public static long connectionIntervalLimit = 1;
	// exchange interval
	private static long exchangeInterval = 10 * 60;

	private static HostInfo local_host;
	// log4j logger
	private static Logger logger = Logger.getLogger(Server.class);

	private static Boolean start(String[] args) throws Exception {

		local_host = new HostInfo(InetAddress.getLocalHost().getHostAddress(), port);

		logger.info("[INFO] - Starting the EZShare Server");
		Options opt = new Options();
		opt.addOption("help", "show all the options on server ");
		opt.addOption("advertisedhostname", true, "advertised hostname");
		opt.addOption("connectionintervallimit", true, "connection interval limit in seconds");
		opt.addOption("exchangeinterval", true, "exchange interval in seconds");
		opt.addOption("port", true, "server port, an integer");
		opt.addOption("sport", true, "secure port number");
		opt.addOption("secret", true, "secret");
		opt.addOption("debug", "print debug information");
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;

		cmd = parser.parse(opt, args);

		if (cmd.hasOption("help")) {
			ServerUtils.printHelpInfo();
			return false;
		}

		if (cmd.hasOption("advertisedhostname")) {
			advertisedHostName = cmd.getOptionValue("advertisedhostname");
		} else {
			advertisedHostName = InetAddress.getLocalHost().getHostAddress().toString();
		}

		if (cmd.hasOption("connectionintervallimit")) {
			connectionIntervalLimit = Long.parseLong(cmd.getOptionValue("connectionintervallimit"));
		}

		if (cmd.hasOption("exchangeinterval")) {
			exchangeInterval = Long.parseLong(cmd.getOptionValue("exchangeinterval"));
		}

		if (cmd.hasOption("port")) {
			port = Integer.parseInt(cmd.getOptionValue("port"));
		}

		if (cmd.hasOption("secret")) {
			secret = cmd.getOptionValue("secret");
		} else {
			secret = ServerUtils.generateSecret();
		}

		logger.info("[INFO] - started");
		return true;
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
		new Thread(() -> ServerUtils.sendHostlist(exchangeInterval * 1000, local_host, host_list)).start();
	}

	public static void main(String[] args) {

		Boolean server_status;

		try {
			server_status = start(args);
			if (server_status) {
				run();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		return;
	}

}
