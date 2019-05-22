package org.unnati.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

class ZookeeperWatcher implements Watcher {

    public void process(WatchedEvent watchedEvent) {
        Event.EventType eventType=watchedEvent.getType();
        Event.KeeperState state=watchedEvent.getState();
        System.out.println("Event is : "+eventType);
        System.out.println("State is : "+state);
        switch (eventType){
            case None:
                switch (state){
                    case AuthFailed:
                        break;
                    case SaslAuthenticated:
                        break;
                    case SyncConnected:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case Expired:
                        break;
                    case Disconnected:
                        break;
                }
                break;
            case NodeCreated:
                break;
            case NodeDataChanged:
                break;
            case NodeDeleted:
                break;
            case NodeChildrenChanged:
                break;
        }
    }
}