package org.unnati.zookeeper.txn.utils;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class ZooKeeperSupport {

    private ZooKeeper zooKeeper;
    private String rootName;
    private List<ACL> aclList;

    public ZooKeeperSupport(ZooKeeper zooKeeper,String rootName){
        this.zooKeeper=zooKeeper;
        this.rootName=rootName;
        this.aclList= ZooDefs.Ids.OPEN_ACL_UNSAFE;
    }

    public String addPersistentNode(String name) throws KeeperException, InterruptedException {
        return this.zooKeeper.create(this.rootName+"/"+name,new byte[0],this.aclList, CreateMode.PERSISTENT);
    }
    public String addPersistentNode(String name,boolean isFullPath) throws KeeperException, InterruptedException {
        return this.zooKeeper.create(name,new byte[0],this.aclList, CreateMode.PERSISTENT);
    }

    public String addEphemeralNode(String name) throws KeeperException, InterruptedException {
        return this.zooKeeper.create(this.rootName+"/"+name,new byte[0],this.aclList, CreateMode.EPHEMERAL);
    }

    public String addEphemeralNode(String name,boolean isFullPath) throws KeeperException, InterruptedException {
        return this.zooKeeper.create(name,new byte[0],this.aclList, CreateMode.EPHEMERAL);
    }
    public void delete(String path) throws KeeperException, InterruptedException {
         this.zooKeeper.delete(path,-1);
    }

    public void setData(String path,String data) throws KeeperException, InterruptedException {
        this.zooKeeper.setData(path,data.getBytes(),-1);
    }

    public void setData(String path,String data,boolean isFullPath) throws KeeperException, InterruptedException {
        this.zooKeeper.setData(path,data.getBytes(),-1);
    }

    public String getData(String path) throws KeeperException, InterruptedException {
        return new String(this.zooKeeper.getData(path,false,new Stat()));
    }

    public String getData(String path, Watcher watcher,boolean isFullPath) throws KeeperException, InterruptedException {
        return new String(this.zooKeeper.getData(path,watcher,null));
    }

    public List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return  this.zooKeeper.getChildren(path,watcher);
    }
}
