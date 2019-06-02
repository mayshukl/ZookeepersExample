package org.unnati.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.unnati.Constants;
import org.unnati.zookeeper.support.ZookeeperSupport;

public class ReadWriteLockImpl implements ReadWriteLock {
    
    private ZookeeperSupport zookeeperSupport;
    private String readLock;
    private String writeLock;
    private String path;
    
    public ReadWriteLockImpl(ZooKeeper zooKeeper){
        this.zookeeperSupport = new ZookeeperSupport(zooKeeper, Constants.LOCK_NODE);
        try {
            if(!this.zookeeperSupport.isExist(Constants.LOCK_NODE,null)){
                this.zookeeperSupport.createPath(Constants.LOCK_NODE);
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.readLock=Constants.READ_LOCK_NODE;
        this.writeLock=Constants.WRITE_LOCK_NODE;
    }
    
    
    @Override
    public void readLock() {
        try {
             this.path=this.zookeeperSupport.createPath(this.readLock);
            this.zookeeperSupport.registerWatcherAndWait(this.path,"write",false);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void writeLock() {
        try {
            this.path=this.zookeeperSupport.createPath(this.writeLock);
            this.zookeeperSupport.registerWatcherAndWait(path,null,false);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void unlock() {
        if(this.path==null){
            throw new IllegalStateException("Unlock can not be called without lock");
        }
        try {
            this.zookeeperSupport.delete(this.path,false);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
