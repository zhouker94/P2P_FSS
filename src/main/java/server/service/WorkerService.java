package server.service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
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

import server.Server;
import server.ServerConfig;
import server.ServerUtils;

public class WorkerService {

    private static WorkerService workerService;
    private ServerSocket serverSocket;
    private Logger logger = Logger.getLogger(WorkerService.class);
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
                ServerSocketFactory.getDefault().createServerSocket(server.port);

        ExecutorService eService =
                Executors.newFixedThreadPool(server.maxConnections);

        System.out.println("Now waiting for connection from client...");

        new Thread(() -> {
            try {
                while (Server.status == ServerUtils.Status.START) {
                    Socket socket = serverSocket.accept();
                    logger.info("[INFO] - new connection from " + socket.getInetAddress() + ":" + socket.getPort());
                    eService.execute(new RequestHandler(socket));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    class RequestHandler implements Runnable {
        private JSONObject request;
        LinkedList<JSONObject> response;
        private Socket socket;

        public RequestHandler(Socket socket) {
            // TODO Auto-generated constructor stub
            this.socket = socket;
        }

        @Override
        public void run() {
            InetAddress clientIpAddress = socket.getInetAddress();
            try {
                // if the client just have connected server, close it
                if (ServerConfig.client_list.contains(clientIpAddress)) {
                    // close the socket
                    socket.close();
                }
                logger.info("[INFO] - new connection from " + socket.getInetAddress() + ":" + socket.getPort());
                ServerUtils.addClientAddress(socket, ServerConfig.client_list);

                // Input stream
                DataInputStream input = new DataInputStream(socket.getInputStream());
                // Output Stream
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                /** get client's command */
                JSONParser parser = new JSONParser();
                request = (JSONObject) parser.parse(input.readUTF());
                logger.info("[INFO] - RECEIVED: " + request.toJSONString());

                LinkedList<JSONObject> response = ServerUtils.parseCommand(request, output, input);

                for (JSONObject result : response) {
                    output.writeUTF(result.toJSONString());
                    output.flush();
                    logger.info("[FINE] - SEND: " + result.toJSONString());
                }

            } catch (IOException | ParseException e) {
                e.printStackTrace();
            } finally {
                try {
                    Thread.sleep(ServerConfig.connectionIntervalLimit * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ServerUtils.removeClientAddress(socket, ServerConfig.client_list);
            }
        }
    }
}
