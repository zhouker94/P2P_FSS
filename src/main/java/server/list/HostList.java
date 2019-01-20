package server.list;

import org.json.simple.JSONArray;
import server.HostInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HostList extends BaseList<HostInfo> {
    private List<HostInfo> hostList;

    public HostList() {
        this.hostList = Collections.synchronizedList(new ArrayList<>());
    }

    public HostInfo getRandom() {

        int host_list_size = this.hostList.size();
        if (host_list_size <= 0) {
            return null;
        }

        HostInfo host;
        host = this.hostList.get(new Random().nextInt(host_list_size));
        return host;
    }

    public JSONArray getAll() {
        JSONArray res = new JSONArray();
        for (HostInfo ht : this.hostList) {
            res.add(ht.toJson());
        }
        return res;
    }

    public void printOut() {
        System.out.println("---------------host---------------");
        for (HostInfo h : hostList) {
            System.out.println(h.toString());
        }
        System.out.println("----------------------------------");
    }

}
