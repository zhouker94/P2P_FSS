package server.list;

import org.json.simple.JSONArray;
import server.HostInfo;

import java.util.Random;

public class HostList extends BaseList<HostInfo> {

    public HostInfo getRandom() {

        int host_list_size = this.list.size();
        if (host_list_size <= 0) {
            return null;
        }

        HostInfo host;
        host = this.list.get(new Random().nextInt(host_list_size));
        return host;
    }

    public JSONArray getAll() {
        JSONArray res = new JSONArray();
        for (HostInfo ht : this.list) {
            res.add(ht.toJson());
        }
        return res;
    }

    public void printOut() {
        System.out.println("---------------host---------------");
        for (HostInfo h : list) {
            System.out.println(h.toString());
        }
        System.out.println("----------------------------------");
    }

}
