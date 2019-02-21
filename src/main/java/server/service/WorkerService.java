package server.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import server.ClientInfo;
import server.Server;
import server.Utils;
import server.RequestProcessor;

public class WorkerService {

    private static WorkerService workerService;
    private ServerSocket serverSocket;
    private Logger LOG = Logger.getLogger(WorkerService.class);
    private Server server;

    public static WorkerService getInstance() {
        if (workerService == null) {
            workerService = new WorkerService();
        }
        return workerService;
    }

    public void start(Server server) throws IOException {
        this.server = server;
        this.serverSocket =
                ServerSocketFactory.getDefault().createServerSocket(server.localHost.getPort());

        ExecutorService eService =
                Executors.newFixedThreadPool(server.maxConnections);

        LOG.info("Now waiting for connection from client...");

        new Thread(() -> {
            try {
                while (Server.status == Utils.Status.START) {
                    Socket socket = serverSocket.accept();
                    LOG.info("[INFO] - new connection from " + socket.getInetAddress() + ":" + socket.getPort());
                    eService.execute(new RequestHandler(socket));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    class RequestHandler implements Runnable {

        private RequestProcessor processor;

        private JSONObject request;
        LinkedList<JSONObject> response;

        private Socket socket;

        RequestHandler(Socket socket) {
            // TODO Auto-generated constructor stub
            this.socket = socket;
            this.processor = new RequestProcessor();
        }

        @Override
        public void run() {
            ClientInfo clientInfo = new ClientInfo(socket.getInetAddress());
            DataInputStream dataInputStream;
            DataOutputStream dataOutputStream;
            try {
                // If the client just have connected server, close it
                if (server.clientList.contains(clientInfo)) {
                    socket.close();
                    return;
                }
                LOG.info("[INFO] - new connection from " +
                        socket.getInetAddress() + ":" + socket.getPort());

                server.clientList.add(clientInfo);

                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                /* get client's command */
                JSONParser parser = new JSONParser();
                request = (JSONObject) parser.parse(dataInputStream.readUTF());
                LOG.info("[INFO] - RECEIVED: " + request.toJSONString());

                response = processor.processRequest(
                        request,
                        dataOutputStream,
                        dataInputStream);

                for (JSONObject result : response) {
                    dataOutputStream.writeUTF(result.toJSONString());
                    dataOutputStream.flush();
                    LOG.info("[FINE] - SEND: " + result.toJSONString());
                }

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(server.connectionIntervalLimit * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                server.clientList.remove(clientInfo);
            }
        }
    }
}
