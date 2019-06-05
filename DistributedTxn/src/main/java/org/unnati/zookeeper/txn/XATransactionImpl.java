package org.unnati.zookeeper.txn;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.unnati.Constants;
import org.unnati.zookeeper.txn.utils.ZooKeeperSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class XATransactionImpl implements XATransaction {

    class ResourceWatcher implements Watcher {

        String GUUID;
        ResourceWatcher(String guuid){
            this.GUUID=guuid;
        }

        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType().equals(Event.EventType.NodeDataChanged)){
                try {
                    System.out.println("Data is "+zooKeeperSupport.getData(watchedEvent.getPath()));
                    count--;
                    if(zooKeeperSupport.getData(watchedEvent.getPath()).equals("ROLLBACK")){
                        commit=commit&&false;
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ChildrenWatcher implements Watcher {

        CountDownLatch latch;

        public ChildrenWatcher(CountDownLatch latch){
            this.latch=latch;
        }

        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType().equals(Event.EventType.NodeChildrenChanged)){
               System.out.println("Path is "+watchedEvent.getPath());
                try {
                    if(zooKeeperSupport.getChildren(watchedEvent.getPath(),null).size()==0){
                        this.latch.countDown();
                    }
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private Map<String,XAResource> xaResourceMap;
    private boolean commit=true;
    private int count;
    private ZooKeeperSupport zooKeeperSupport;
    private String prefix="/xa-txn-";
    private String resourcePrefix="res-";
    private String node;
    private long timeOut;
    private long sleepBeforeCheck;

    public XATransactionImpl(){
        xaResourceMap=new HashMap<String, XAResource>();
        timeOut=10000L;
        sleepBeforeCheck=1000L;
        this.node=this.prefix+this.hashCode();
        try {
            this.zooKeeperSupport=new ZooKeeperSupport(new ZooKeeper(Constants.ZOOKEEPER_HOST+":"+Constants.ZOOKEEPER_PORT,10000,null),this.node);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void beginTransaction() {
        try {
            this.zooKeeperSupport.addPersistentNode(this.node,false);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void register(XAResource xaResource) {
        xaResource.registerWatch(this.node+"/"+resourcePrefix+(++count),new ResourceWatcher(new String(resourcePrefix+count)));
        xaResourceMap.put(resourcePrefix+count,xaResource);
        System.out.println("Count is "+count);
    }
    public void commit() {
        if(xaResourceMap.size()==0){
            throw new RuntimeException("no Resource is attached");
        }
        for(XAResource xaResource:xaResourceMap.values()){
            xaResource.prepare();
        }
        long currentTime=System.currentTimeMillis();
        boolean state=false;
        while(currentTime<currentTime+timeOut) {
            System.out.println("Count is " + count);
            if (count == 0) {
                System.out.println("Commit is " + this.commit);
                if (this.commit) {
                    for (XAResource xaResource : xaResourceMap.values()) {
                        xaResource.commit();
                    }
                    state = true;
                }
                break;
                } else {
                    try {
                        Thread.sleep(sleepBeforeCheck);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
         if(state){
             System.out.println("2PC committed");
             doCleanUp();
         }else{
             this.rollBack();
         }

    }

    public void rollBack() {
        for(XAResource xaResource:xaResourceMap.values()){
            xaResource.rollback();
        }
        System.out.println("2PC Rollback");
        doCleanUp();
    }

    private void doCleanUp(){
        CountDownLatch latch=new CountDownLatch(1);
        ChildrenWatcher childrenWatcher=new ChildrenWatcher(latch);
        List<String> children= null;
        try {
            children = this.zooKeeperSupport.getChildren(this.node,childrenWatcher);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(children.size()>0){
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        xaResourceMap.clear();
        try {
            this.zooKeeperSupport.delete(this.node);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
