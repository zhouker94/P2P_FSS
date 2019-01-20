package server.service;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import server.hostlist.HostInfo;
import server.Server;
import server.ServerUtils.*;
import server.hostlist.HostList;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;

public class HeartbeatService implements Runnable {

    private static HeartbeatService heartbeatService;
    private HostList hostList;
    private Logger LOG = Logger.getLogger(HeartbeatService.class);
    private long exchangeInterval;

    public static HeartbeatService getInstance() {
        if (heartbeatService == null) {
            heartbeatService = new HeartbeatService();
        }
        return heartbeatService;
    }

    public void start(long exchangeInterval) {
        this.exchangeInterval = exchangeInterval;
        this.hostList = new HostList();

        // Start a new thread to send heartbeat
        new Thread(heartbeatService).start();
    }

    @Override
    public void run() {
        HostInfo selectedHost;

        while (Server.status != Status.STOP) {
            try {
                Thread.sleep(this.exchangeInterval);
            } catch (Exception e) {
                LOG.info(e);
            }

            hostList.printOut();

            if (!hostList.isEmpty()) {
                selectedHost = hostList.getRandom();

                // check if local host
                boolean isLocalHost = false;
                if (selectedHost.toString().equals("localhost")) {
                    isLocalHost = true;
                    selectedHost.setHostname(local_host.getHostname());
                }

                if (selectedHost.equals(local_host)) {
                    continue;
                } else if (isLocalHost) {
                    hostList.remove(selectedHost);
                }

                JSONObject exchangeCommand = new JSONObject();
                exchangeCommand.put("command", "EXCHANGE");
                exchangeCommand.put("serverList", hostList.getAll());

                Socket socket;
                try {
                    socket = new Socket(selectedHost.getHostname(), selectedHost.getPort());
                    LinkedList<JSONObject> res =
                            sendToServer(exchangeCommand, socket);
                    System.out.println(Arrays.toString(res.toArray()));
                    socket.close();
                } catch (IOException e) {
                    // remove
                    System.out.println("IO Exception");
                    hostList.remove(selectedHost);
                }
            }

        }
    }

    private static LinkedList<JSONObject>
    sendToServer(JSONObject request, Socket sc) {

        LinkedList<JSONObject> resultList = new LinkedList<>();

        try {
            DataInputStream inputStream;
            DataOutputStream outputStream;

            inputStream = new DataInputStream(sc.getInputStream());
            outputStream = new DataOutputStream(sc.getOutputStream());
            outputStream.writeUTF(request.toJSONString());
            outputStream.flush();
            JSONParser parser = new JSONParser();

            /* receive JSONObject */

            while (true) {
                JSONObject result;
                result = (JSONObject) parser.parse(inputStream.readUTF());
                if (result.containsKey("response")) {
                    continue;
                }
                if (result.containsKey("resultSize")) {
                    continue;
                }
                resultList.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

}
