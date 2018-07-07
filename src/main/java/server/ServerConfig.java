package server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import resource.Resource;
import resource.ResourceKey;

public class ServerConfig {

	public static int port = 33254;
	// Hash map of resources
	public static HashMap<ResourceKey, Resource> resource_map = new HashMap<ResourceKey, Resource>();
	public static int maxConnections = 100;
	// A list of Server Records
	public static ArrayList<HostInfo> host_list = new ArrayList<HostInfo>();
	// store those clients' addresses who just used the server
	public static ArrayList<InetAddress> client_list = new ArrayList<InetAddress>();
	// Server's secret
	public static String secret = "";
	// advertised host name
	public static String advertisedHostName = "";
	// connection interval limit
	public static long connectionIntervalLimit = 1;
	// exchange interval
	public static long exchangeInterval = 10 * 60;

	public static HostInfo local_host;
}
