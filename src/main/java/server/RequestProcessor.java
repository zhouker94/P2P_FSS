package server;

import org.json.simple.JSONObject;

public interface RequestProcessor {
	
    class RequestProcessorException extends Exception {
        public RequestProcessorException(String msg, Throwable t) {
            super(msg, t);
        }
    }

    void processRequest(JSONObject request) throws RequestProcessorException;

}
