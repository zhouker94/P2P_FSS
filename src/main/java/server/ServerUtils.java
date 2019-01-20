package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import org.json.simple.parser.ParseException;
import server.command.AbstractCommand;
import server.command.PublishCommand;

public class ServerUtils {

    public enum Status {
        START, STOP, JOIN
    }

    public static void printHelpInfo() {
        System.out.println("usage:Server [-advertisedhostname <arg>][-debug][-connectionintervallimit <arg>]");
        System.out.println("[-exchangeinterval <arg>][-port <arg>][-secret <arg>]");
        System.out.println("Server for Unimelb COMP90015");
        System.out.println();
        System.out.println("-advertisedhostname <arg> advertised hostname");
        System.out.println("-connectionintervallimit <arg> connection interval limit in seconds");
        System.out.println("-exchangeinterval <arg> exchange interval in seconds");
        System.out.println("-port <arg> server port, an integer");
        System.out.println("-secret <arg> secret");
        System.out.println("-debug print debug information");
        System.out.println("-sport <arg> secure port number");
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
     * @param socket
     * @param
     * @return
     */
    public static void addClientAddress(Socket socket,
                                        ArrayList<InetAddress> client_list) {

        client_list.add(socket.getInetAddress());
    }

    /**
     * @param socket
     * @param client_list
     * @return
     */
    public static void removeClientAddress(Socket socket,
                                           ArrayList<InetAddress> client_list) {

        InetAddress c_address = socket.getInetAddress();
        if (client_list.contains(c_address)) {
            client_list.remove(c_address);
        }
    }

    @SuppressWarnings("unchecked")
    public static LinkedList<JSONObject> parseCommand(JSONObject clientCommand, DataOutputStream output,
                                                      DataInputStream input) {
        LinkedList<JSONObject> results = new LinkedList<JSONObject>();
        JSONObject result = new JSONObject();

        if (clientCommand.containsKey("command")) {
            AbstractCommand command;
            switch (clientCommand.get("command").toString()) {
                case "PUBLISH":
                    command = new PublishCommand();
                    result = command.commandRun(clientCommand);
                    results.add(result);
                    break;
                case "REMOVE":
                    /*
                    result = remove(clientCommand);
                    results.add(result);
                    */
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
		/*
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
			}
		*/
        return results;
    }

    public static JSONObject stringToJSON(String msg) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        jsonObject = (JSONObject) jsonParser.parse(msg);
        return jsonObject;
    }
}
