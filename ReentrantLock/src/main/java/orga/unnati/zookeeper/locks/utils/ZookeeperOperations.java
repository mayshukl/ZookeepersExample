package orga.unnati.zookeeper.locks.utils;

import org.apache.zookeeper.KeeperException;

public interface ZookeeperOperations {
    
    public Object execute() throws KeeperException,InterruptedException;
}
