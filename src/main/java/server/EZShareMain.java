package server;

import java.net.UnknownHostException;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;


public class EZShareMain {

    // log4j logger
    private static final Logger LOG = Logger.getLogger(Server.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
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

    private void initializeAndRun(String[] args)
            throws ParseException, UnknownHostException {

        LOG.info("[INFO] - Initializing EZShare Server");

        ServerConfig config = new ServerConfig();
        config.parse(args);

        LOG.info("Starting server");
        final Server ezServer = new Server(config);
        ezServer.start();
    }

}
