package server.event;

import java.util.ArrayList;
import java.util.List;

import server.HostInfo;
import server.resource.Resource;

public class SubscribeListenerManager {
    private List<SubscribeEventListener> subscribeListeners = null;

    public SubscribeListenerManager(){
        this.subscribeListeners = new ArrayList<SubscribeEventListener>();
    }

    public void addSubscribeEventListener(SubscribeEventListener listener){
        subscribeListeners.add(listener);
    }

    public void deleteSubscribeEventListener(SubscribeEventListener listener){
        subscribeListeners.remove(listener);
    }

    //notify the subscribers of newly published and shared resources
    public void informSubscribersOfNewResource(Resource res){
        for( SubscribeEventListener subscriber : this.subscribeListeners){
            //matching new resource with the template and sending it to the client if matched.
            subscriber.mactchTemplate(res);
        }
    }

    //Ask all the subscribers to subscribe to new server.
    public void informSubscribersOfNewServer(HostInfo ht){
        for( SubscribeEventListener subscriber : this.subscribeListeners){
            subscriber.subscribeToServer(ht);
        }
    }
}
