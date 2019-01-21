package server.service;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import server.Utils;
import server.HostInfo;
import server.Server;
import server.Utils.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class HeartbeatService implements Runnable {

    private static HeartbeatService heartbeatService;
    private Logger LOG = Logger.getLogger(HeartbeatService.class);
    private Server server;

    public static HeartbeatService getInstance() {
        if (heartbeatService == null) {
            heartbeatService = new HeartbeatService();
        }
        return heartbeatService;
    }

    public void start(Server server) {
        this.server = server;
        LOG.info("Start exchanging massage");
        new Thread(heartbeatService).start();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        while (Server.status == Status.START) {
            try {
                Thread.sleep(server.exchangeInterval);
            } catch (InterruptedException e) {
                LOG.info(e);
            }

            server.hostList.printOut();

            if (!server.hostList.isEmpty()) {
                HostInfo selectedHost;
                selectedHost = server.hostList.getRandom();

                JSONObject exchangeCommand = new JSONObject();
                exchangeCommand.put("command", "EXCHANGE");
                exchangeCommand.put("serverList", server.hostList.getAll());

                Socket socket;
                try {
                    socket = new Socket(selectedHost.getHostname(), selectedHost.getPort());
                    JSONObject res =
                            sendToServer(exchangeCommand, socket);
                    LOG.info(res.toString());
                    socket.close();
                } catch (IOException ioe) {
                    // remove the server without response
                    LOG.info("Sever " + selectedHost.toString() + " is not alive!");
                    server.hostList.remove(selectedHost);
                } catch (ParseException pe) {
                    LOG.info("Json Parse Exception");
                }
            }
        }
    }

    private static JSONObject
    sendToServer(JSONObject request, Socket sc) throws IOException, ParseException {
        DataInputStream inputStream;
        DataOutputStream outputStream;
        JSONObject response;

        inputStream = new DataInputStream(sc.getInputStream());
        outputStream = new DataOutputStream(sc.getOutputStream());

        outputStream.writeUTF(request.toJSONString());
        outputStream.flush();
        String responseStr = inputStream.readUTF();

        /* receive JSONObject */
        response = Utils.stringToJSON(responseStr);

        return response;
    }

}
