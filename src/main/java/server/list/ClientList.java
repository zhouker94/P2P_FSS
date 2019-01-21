package server.list;

import server.ClientInfo;
import server.HostInfo;

import java.util.List;

public class ClientList extends BaseList <ClientInfo> {
    private List<ClientInfo> clientList;

    @Override
    public boolean isEmpty() {
        return false;
    }
}
