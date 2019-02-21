package server;

import org.json.simple.JSONObject;
import server.command.AbstractCommand;
import server.command.PublishCommand;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.LinkedList;

public class RequestProcessor {
	
    class RequestProcessorException extends Exception {
        public RequestProcessorException(String msg, Throwable t) {
            super(msg, t);
        }
    }

    public LinkedList<JSONObject> processRequest(JSONObject clientCommand,
                                          DataOutputStream output,
                                          DataInputStream input) {

        LinkedList<JSONObject> response = new LinkedList<>();
        JSONObject result = new JSONObject();

        if (clientCommand.containsKey("command")) {
            AbstractCommand command;
            switch (clientCommand.get("command").toString()) {
                case "PUBLISH":
                    command = new PublishCommand();
                    result = command.commandRun(clientCommand);
                    response.add(result);
                    break;
                case "REMOVE":
                    /*
                    result = remove(clientCommand);
                    results.add(result);
                    */
                    break;
                default:
                    result.put("response", "error");
                    response.add(result);
                    break;
            }
        } else {
            result.put("response", "error");
            response.add(result);
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
        return response;
    }

}
