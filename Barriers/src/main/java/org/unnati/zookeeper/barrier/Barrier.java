package org.unnati.zookeeper.barrier;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.unnati.Constants;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class Barrier implements AutoCloseable {

    private static String path;
    private Watcher watcher;
    private ZooKeeper zookeeper;
    private Semaphore semaphore;


    public Barrier() {
        Throwable throwable=null;
        try {
            this.semaphore = new Semaphore(1);
            this.watcher = new BarrierWatcher(this.semaphore);
            this.zookeeper = new ZooKeeper(Constants.ZOOKEEPER_HOST + ":" + Constants.ZOOKEEPER_PORT, 10000, this.watcher);
         }catch (IOException e) {
            e.printStackTrace();
            throwable=e;
        }
        if(throwable!=null){
            throw new RuntimeException("Barrier can not be created",throwable);
        }
    }

    public void createBarrierNode() throws InterruptedException {
        try {
            this.path=this.zookeeper.create(Constants.BARRIER_NODE, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            e.printStackTrace();
            throw new IllegalStateException("Barrier is not in expected state",e); 
        } 
    }

    public void await() throws InterruptedException{
        System.out.println(this.path);
       try {
           if (this.zookeeper.exists(this.path, this.watcher) != null) {
               this.semaphore.acquire();
           }
       }catch (KeeperException e) {
           throw new IllegalStateException("Barrier is not in expected state",e);
                   
       }
    }

    public void deleteBarrierNode() throws InterruptedException {
        try {
             this.zookeeper.delete(Constants.BARRIER_NODE,-1);
        } catch (KeeperException e) {
            e.printStackTrace();
            throw new IllegalStateException("Barrier is not in expected state",e);
        }
    }
    

    public void close() throws InterruptedException {
        this.zookeeper.close();
    }
    
    @Override  
    public void finalize(){
        try {
            this.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
