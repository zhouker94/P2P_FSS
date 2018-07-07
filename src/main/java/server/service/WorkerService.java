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
import server.util.ServerUtils;

public class WorkerService {
	private static WorkerService workerService = null;
	private ServerSocket serverSocket;
	private Logger logger = Logger.getLogger(Server.class);

	private WorkerService() throws IOException {
		this.serverSocket = ServerSocketFactory.getDefault().createServerSocket(Server.port);
	}

	public static WorkerService getInstance() throws IOException {
		if (workerService == null) {
			workerService = new WorkerService();
		}
		return workerService;
	}

	public void startService() {
		ExecutorService eService = Executors.newFixedThreadPool(Server.numConnections);
		new Thread() {
			@Override
			public void run() {
				try {
					while (true) {
						Socket socket = serverSocket.accept();
						logger.info("[INFO] - new connection from " + socket.getInetAddress() + ":" + socket.getPort());
						eService.execute(new RequestHandler(socket));
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}.start();
	}

	class RequestHandler implements Runnable {
		private JSONObject request;
		LinkedList<JSONObject> response;
		private Socket socket;

		public RequestHandler(Socket socket) throws IOException {
			// TODO Auto-generated constructor stub
			this.socket = socket;
		}

		@Override
		public void run() {
			InetAddress clientIpAddress = socket.getInetAddress();
			try {
				// if the client just have connected server, close it
				if (Server.client_list.contains(clientIpAddress)) {
					// close the socket
					socket.close();
				}
				logger.info("[INFO] - new connection from " + socket.getInetAddress() + ":" + socket.getPort());
				ServerUtils.addClientAddress(socket, Server.client_list);
				
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
				
			}catch (IOException | ParseException e) {
				e.printStackTrace();
			}finally {
				try {
					Thread.sleep(Server.connectionIntervalLimit * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ServerUtils.removeClientAddress(socket, Server.client_list);
			}
		}
	}
}
