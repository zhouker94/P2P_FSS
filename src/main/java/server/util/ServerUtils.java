package server.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import server.HostInfo;

public class ServerUtils {

	public static synchronized void printHostList(ArrayList<HostInfo> host_list) {
		System.out.println("----------------------host----------------------------");
		for (HostInfo h : host_list) {
			System.out.println(h.toString());
		}
	}

	public static HostInfo getRandomHost(ArrayList<HostInfo> host_list) {

		int size = host_list.size();
		if (size <= 0) {
			return null;
		}

		HostInfo host = null;
		Random random = new Random();
		host = host_list.get(random.nextInt(size));

		return host;
	}

	public static void printHelpInfo() {
		System.out.println("usage:Server [-advertisedhostname <arg>][-debug][-connectionintervallimit <arg>]");
		System.out.println("      [-exchangeinterval <arg>][-port <arg>][-secret <arg>]");
		System.out.println("Server for Unimelb COMP90015");
		System.out.println();
		System.out.println("-advertisedhostname <arg>		advertised hostname");
		System.out.println("-connectionintervallimit <arg> 	connection interval limit in seconds");
		System.out.println("-exchangeinterval <arg> 		exchange interval in seconds");
		System.out.println("-port <arg> 					server port, an integer");
		System.out.println("-secret <arg>					secret");
		System.out.println("-debug							print debug information");
		System.out.println("-sport <arg>					secure port number");
	}

	/**
	 * generateSecret
	 * 
	 * @return secret string
	 */
	public static String generateSecret() {
		String secret = "";
		Random random = new Random();
		for (int i = 0; i < 26; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			if ("char".equalsIgnoreCase(charOrNum)) {
				int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
				secret += (char) (random.nextInt(26) + temp);
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				secret += String.valueOf(random.nextInt(10));
			}
		}
		return secret;
	}

	/**
	 * send the request to another server as an client
	 * 
	 * @return LinkedList<JSONObject>
	 */
	public static LinkedList<JSONObject> sendToServer(JSONObject request, Socket sc) {
		LinkedList<JSONObject> results = new LinkedList<JSONObject>();
		JSONObject result = new JSONObject();
		DataInputStream input = null;
		DataOutputStream output = null;
		try {
			input = new DataInputStream(sc.getInputStream());
			output = new DataOutputStream(sc.getOutputStream());
			output.writeUTF(request.toJSONString());
			output.flush();
			JSONParser parser = new JSONParser();
			/** receive JSONObject */
			while (true) {
				result = (JSONObject) parser.parse(input.readUTF().toString());
				if (result.containsKey("response")) {
					continue;
				}
				if (result.containsKey("resultSize")) {
					continue;
				}
				results.add(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return results;
	}

	/**
	 * @param selectedHost
	 * @param host_list
	 * @return
	 */
	public static synchronized void removeHost(HostInfo selectedHost, ArrayList<HostInfo> host_list) {

		if (host_list.contains(selectedHost)) {
			host_list.remove(selectedHost);
		}
	}

	/**
	 * @param socket
	 * @param clientAddresses
	 * @return
	 */
	public static synchronized void addClientAddress(Socket socket,
			ArrayList<InetAddress> client_list) {

		client_list.add(socket.getInetAddress());
	}

	/**
	 * @param socket
	 * @param client_list
	 * @return
	 */
	public static synchronized void removeClientAddress(Socket socket,
			ArrayList<InetAddress> client_list) {
		
		InetAddress c_address = socket.getInetAddress();
		if (client_list.contains(c_address)) {
			client_list.remove(c_address);
		}
	}
	
	/**
	 * @param interval
	 * @param local_host
	 * @param host_list
	 */
	@SuppressWarnings("unchecked")
	public static void sendHostlist(long interval, HostInfo local_host, ArrayList<HostInfo> host_list) {

		HostInfo selectedHost = null;
		JSONObject exchangeCommand = new JSONObject();

		while (true) {
			JSONArray serverList = new JSONArray();
			try {
				Thread.sleep(interval);
			} catch (Exception e) {

			}

			synchronized (host_list) {
				printHostList(host_list);
				// not empty
				if (!host_list.isEmpty()) {
					selectedHost = getRandomHost(host_list);

					// not himself
					boolean localHostFlag = false;
					if (selectedHost.getHostname().equals("localhost")) {
						localHostFlag = true;
						selectedHost.setHostname(local_host.getHostname());
					}
					if (selectedHost.equals(local_host)) {
						continue;
					} else if (localHostFlag) {
						removeHost(selectedHost, host_list);
					}
					for (HostInfo ht : host_list) {
						serverList.add(ht.toJson());
					}
					exchangeCommand.put("command", "EXCHANGE");
					exchangeCommand.put("serverList", serverList);

					Socket sc;
					try {
						sc = new Socket(selectedHost.getHostname(), selectedHost.getPort());
						sendToServer(exchangeCommand, sc);
						sc.close();
					} catch (IOException e) {
						// remove
						System.out.println("IO Exception");
						removeHost(selectedHost, host_list);
					}
				}
			}
		}
	}
	
	public static LinkedList<JSONObject> parseCommand(JSONObject clientCommand, DataOutputStream output,
			DataInputStream input) {
		LinkedList<JSONObject> results = new LinkedList<JSONObject>();
		JSONObject result = new JSONObject();
		/* TODO:
		if (clientCommand.containsKey("command")) {
			switch (clientCommand.get("command").toString()) {
			case "PUBLISH":
				result = publish(clientCommand);
				results.add(result);
				break;
			case "REMOVE":
				result = remove(clientCommand);
				results.add(result);
				break;
			case "QUERY":
				results = query(clientCommand);
				break;
			case "FETCH":
				fetch(clientCommand, output);
				break;
			case "SHARE":
				result = share(clientCommand);
				results.add(result);
				break;
			case "EXCHANGE":
				result = exchange(clientCommand);
				results.add(result);
				break;
			case "SUBSCRIBE":
				result = subscribe(clientCommand, output, input);
				results.add(result);
				break;
			default:
				result.put("response", "error");
				results.add(result);
				break;
			}
		} else {
			result.put("response", "error");
			results.add(result);
		}
		*/
		return results;
	}
}
