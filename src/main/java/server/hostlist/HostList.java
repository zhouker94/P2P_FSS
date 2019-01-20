package server.hostlist;

import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HostList {
    private List<HostInfo> hostList;

    public HostList(){
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

    public void remove(HostInfo selectedHost) {
        hostList.remove(selectedHost);
    }

    public JSONArray getAll(){
        JSONArray res = new JSONArray();
        for (HostInfo ht : this.hostList) {
            res.add(ht.toJson());
        }
        return res;
    }

    public boolean isEmpty(){
        return hostList.isEmpty();
    }

    public void printOut() {
        System.out.println("---------------host---------------");
        for (HostInfo h : hostList) {
            System.out.println(h.toString());
        }
        System.out.println("----------------------------------");
    }

}
