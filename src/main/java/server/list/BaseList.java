package server.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BaseList<ItemType> {

    private List<ItemType> list;

    BaseList() {
        this.list =
                Collections.synchronizedList(new ArrayList<>());
    }

    public void add(ItemType item) {
        this.list.add(item);
    }

    public void remove(ItemType item) {
        this.list.remove(item);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }
}
