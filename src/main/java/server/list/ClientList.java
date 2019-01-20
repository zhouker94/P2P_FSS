package server.list;

import org.json.simple.JSONArray;
import server.ClientInfo;

public class ClientList extends BaseList <ClientInfo> {

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public JSONArray getAll() {
        return null;
    }

    @Override
    public void printOut() {

    }
}
