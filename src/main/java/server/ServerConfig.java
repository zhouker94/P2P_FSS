package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ServerConfig {

	// Server's configuration parameters

	// defaults to 33254 if not set explicitly
	protected int port = 33254;
	protected int maxConnections = 100;

	// Server's secret
	protected String secret = "";
	// advertised host name
	protected String advertisedHostName = "";
	// connection interval limit
	protected long connectionIntervalLimit = 1;
	// exchange interval
	protected long exchangeInterval = 10 * 60;

	protected HostInfo local_host;
	// store those clients' addresses who just used the server
	protected List<InetAddress> client_list;

	/**
	 * @param args
	 * @throws UnknownHostException
	 * @throws ParseException
	 */
	public void parse(String[] args) throws UnknownHostException, ParseException {

		Options opt = new Options();
		opt.addOption("help", "show all the options on server ");
		opt.addOption("advertisedhostname", true, "advertised hostname");
		opt.addOption("connectionintervallimit", true, "connection interval limit in seconds");
		opt.addOption("exchangeinterval", true, "exchange interval in seconds");
		opt.addOption("port", true, "server port, an integer");
		opt.addOption("sport", true, "secure port number");
		opt.addOption("secret", true, "secret");
		opt.addOption("debug", "print debug information");

		CommandLine cmd = new DefaultParser().parse(opt, args);

		if (cmd.hasOption("help")) {
			ServerUtils.printHelpInfo();
			System.exit(0);
		}

		if (cmd.hasOption("advertisedhostname")) {
			this.advertisedHostName = cmd.getOptionValue("advertisedhostname");
		} else {
			this.advertisedHostName = InetAddress.getLocalHost().getHostAddress().toString();
		}

		if (cmd.hasOption("connectionintervallimit")) {
			this.connectionIntervalLimit = Long.parseLong(cmd.getOptionValue("connectionintervallimit"));
		}

		if (cmd.hasOption("exchangeinterval")) {
			this.exchangeInterval = Long.parseLong(cmd.getOptionValue("exchangeinterval"));
		}

		if (cmd.hasOption("port")) {
			this.port = Integer.parseInt(cmd.getOptionValue("port"));
		}

		if (cmd.hasOption("secret")) {
			this.secret = cmd.getOptionValue("secret");
		} else {
			this.secret = ServerUtils.generateSecret();
		}

		this.local_host = new HostInfo(InetAddress.getLocalHost().getHostAddress(), this.port);
		this.client_list = Collections.synchronizedList(new ArrayList<InetAddress>());

	}
}
