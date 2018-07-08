/**
 * 
 */
package server;

import java.net.UnknownHostException;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

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
