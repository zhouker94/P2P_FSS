package server;

import org.json.simple.JSONObject;

public interface RequestProcessor {
	
    @SuppressWarnings("serial")
    public static class RequestProcessorException extends Exception {
        public RequestProcessorException(String msg, Throwable t) {
            super(msg, t);
        }
    }

    void processRequest(JSONObject request) throws RequestProcessorException;

}
