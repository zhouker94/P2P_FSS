package server;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ServerConfig {

	// Server's configuration parameters

	// defaults to 33254 if not set explicitly
	int port = 33254;
	int maxConnections = 100;

	// Server's secret
	String secret = "";
	// advertised host name
	String advertisedHostName = "";
	// connection interval limit
	long connectionIntervalLimit = 1;
	// exchange interval
	long exchangeInterval = 10 * 60;

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
			Utils.printHelpInfo();
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
			this.secret = Utils.generateSecret();
		}

	}
}
