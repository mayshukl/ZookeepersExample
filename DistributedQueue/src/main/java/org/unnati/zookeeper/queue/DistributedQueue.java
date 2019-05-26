package org.unnati.zookeeper.queue;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class DistributedQueue {
    Logger logger=Logger.getLogger(DistributedQueue.class.getName());
    {
        Handler handler=new ConsoleHandler() ;
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
       // logger.addHandler(handler);
        logger.setLevel(Level.ALL);
    }
    
    private ZooKeeper zooKeeper;
    
    private String queueNode;
    private List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
    private final String prefix = "qn-";
    
    public DistributedQueue(ZooKeeper zooKeeper, String queueNode, List<ACL> acl) throws KeeperException, InterruptedException {
        this.zooKeeper=zooKeeper;
        this.queueNode=queueNode;
        if(acl!=null){
            this.acl=acl;
        }
        
        if(this.zooKeeper.exists(this.queueNode,null)==null){
            this.zooKeeper.create(this.queueNode,new byte[0],this.acl,CreateMode.PERSISTENT); 
        }
    }
    
    private TreeSet<String> getOrderedChildren(Watcher watcher) throws KeeperException, InterruptedException {
        TreeSet<String> treeSet=new TreeSet<String>();
        List<String> children=this.zooKeeper.getChildren(this.queueNode,watcher);
        for(String name:children){
            treeSet.add(name);
        }
        return treeSet;
    }
    
    private void createNode(byte[] element) throws KeeperException, InterruptedException {
        this.zooKeeper.create(this.queueNode+"/"+this.prefix,element,this.acl, CreateMode.PERSISTENT_SEQUENTIAL);
    }

    private byte[] getData(String name) throws KeeperException, InterruptedException {
       return this.zooKeeper.getData(this.queueNode+"/"+name,false,null);
    }

    private void deleteNode(String name) throws KeeperException, InterruptedException {
        this.zooKeeper.delete(this.queueNode+"/"+name,-1);
    }
    
    class ChildrenWatcher implements Watcher{

        private CountDownLatch latch;

        ChildrenWatcher(){
            this.latch=new CountDownLatch(1);
        }
        
        public void process(WatchedEvent watchedEvent) {
            if(watchedEvent.getType()== Event.EventType.NodeCreated){
                latch.countDown();
            }
        }
        
        public void await() throws InterruptedException {
            this.latch.await();
        }
    }
    
    
    public byte[] poll() throws KeeperException, InterruptedException {
        while(true){
            ChildrenWatcher childrenWatcher =new ChildrenWatcher();
            TreeSet<String> children=this.getOrderedChildren(childrenWatcher);
            if(children.size()==0){
                childrenWatcher.await();
                continue;
            }
            String child=children.first();
            try{
                byte[] bytes=this.getData(child);
                this.deleteNode(child);
                System.out.println(Thread.currentThread().getName() + " retrieved : "+new String(bytes));
                return bytes;
            }catch (KeeperException.NoNodeException e){
                System.out.println("Some one else has deleted the node");
            }
        }
    }
    
    public void offer(byte[] element) throws KeeperException, InterruptedException {
         this.createNode(element);
        System.out.println(Thread.currentThread().getName() + " offered : "+new String(element));
    }
}
