package org.unnati.zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.unnati.zookeeper.utils.ZookeeperSupport;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static org.unnati.Constants.LEADER_NODE;
import static org.unnati.Constants.PROCESS_NODE;
import static org.unnati.Constants.ZOOKEEPER_HOST;
import static org.unnati.Constants.ZOOKEEPER_PORT;

public class Process {

    public static volatile Integer counter=1;

    private boolean isLeader;
    private ZookeeperSupport zookeeperSupport;

    private String id;

    public Process(){
        try {
            this.id=counter++ +"";
            zookeeperSupport=new ZookeeperSupport(new ZooKeeper(ZOOKEEPER_HOST+":"+ZOOKEEPER_PORT,10000,null),LEADER_NODE);
            CountDownLatch latch=new CountDownLatch(1);
            zookeeperSupport.createPath(PROCESS_NODE,new CreateCallback(this.id,latch));
            latch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    public String getId(){
        return id;
    }

    public Boolean isLeader(){
        return this.isLeader;
    }

    public void setIsLeader(Boolean isLeader){
        this.isLeader=isLeader;
    }


    public void read(){
        System.out.println(Thread.currentThread().getName() +" reading via Process"+this.id);
    }

    public void write(){
        if(this.isLeader){
            System.out.println(Thread.currentThread().getName() +" writing via Process"+this.id);
        }else{
            DistributedProcess.getLeader().write();
        }
    }


    class ProcessWatcher implements Watcher{

        String id;

        public ProcessWatcher(String id){
            this.id=id;

        }

        @Override
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType().equals(Event.EventType.NodeDeleted)){
                isLeader=true;
                try {
                    zookeeperSupport.setDataOnRoot(id);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class CreateCallback implements  AsyncCallback.StringCallback{
        String id;
        CountDownLatch latch;
        CreateCallback(String id, CountDownLatch latch){
            this.id=id;
            this.latch=latch;
        }
        @Override
        public void processResult(int i, String path, Object o, String name) {
            try {
                zookeeperSupport.registerWatcher(name,null,false,new ProcessWatcher(id));
                if(zookeeperSupport.findMinChildren(name,null)==null){
                    isLeader=true;
                    try {
                        zookeeperSupport.setDataOnRoot(id);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        }
    }
}
