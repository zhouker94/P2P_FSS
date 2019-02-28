package server;

import org.json.simple.JSONObject;

public class HostInfo {

    private String hostname;
    private int port;
    private String secret;

    /**
     * @param hostname
     * @param port
     * @param secret
     */
    HostInfo(String hostname, int port, String secret) {
        this.port = port;
        this.hostname = hostname;
        this.secret = secret;
    }

    public int getPort() {
        return port;
    }

    public String getHostname() {
        return hostname;
    }

    /**
     * @return host information in JSON format
     */
    @SuppressWarnings("unchecked")
    public JSONObject toJson() {
        JSONObject host_json = new JSONObject();
        host_json.put("hostname", getHostname());
        host_json.put("port", String.valueOf(getPort()));
        return host_json;
    }

    /**
     * @param host
     * @return
     */
    public boolean equals(HostInfo host) {
        return this.hostname.equals(host.getHostname()) && (this.getPort() == host.getPort());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return hostname + ": " + port;
    }
}
