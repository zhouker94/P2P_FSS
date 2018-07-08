/**
 * 
 */
package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import resource.Resource;
import resource.ResourceKey;
import server.Server.State;
import server.util.ServerUtils;

/**
 * @author OwenZhu
 *
 */
public class EZShareMain {

	// log4j logger
	private static final Logger LOG = Logger.getLogger(Server.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EZShareMain main = new EZShareMain();
		try {
			main.initializeAndRun(args);
		} catch (ParseException e) {
			LOG.error("Invalid arguments, exiting abnormally", e);
			System.err.println("Invalid arguments, exiting abnormally");
			System.exit(2);
		} catch (UnknownHostException e) {
			LOG.error("Unable to get local ip address, exiting abnormally", e);
			System.err.println("Unable to get local ip address, exiting abnormally");
			System.exit(2);
		}
	}

	protected void initializeAndRun(String[] args) throws ParseException, UnknownHostException {

		ServerConfig config = new ServerConfig();
		config.parse(args);

		LOG.info("[INFO] - Initializing EZShare Server");
		runFromConfig(config);
	}

	public void runFromConfig(ServerConfig config) {
		final Server ezServer = new Server(config);
	}
}
