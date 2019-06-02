package orga.unnati.zookeeper.locks.utils;


import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import orga.unnati.zookeeper.locks.exceptions.RetryCountExceededException;

import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

public class ZookeeperSupport {
    
    private int retryCount;
    private long waitBeforeRetry;
    private ZooKeeper zooKeeper;
    private String lockNodePath;
    List<ACL> aclList= ZooDefs.Ids.OPEN_ACL_UNSAFE;
    
    public ZookeeperSupport(ZooKeeper zooKeeper,String lockNodePath,int retryCount,long waitBeforeRetry){
        this.zooKeeper=zooKeeper;
        this.waitBeforeRetry=waitBeforeRetry;
        this.retryCount=retryCount+1;
        this.lockNodePath=lockNodePath;
    }
    public ZookeeperSupport(ZooKeeper zooKeeper,String lockNodePath,int retryCount){
        new ZookeeperSupport(zooKeeper,lockNodePath,retryCount,0);
    }
    public ZookeeperSupport(ZooKeeper zooKeeper,String lockNodePath,long waitBeforeRetry){
        new ZookeeperSupport(zooKeeper,lockNodePath,0,waitBeforeRetry);
    }
    public ZookeeperSupport(ZooKeeper zooKeeper,String lockNodePath) {
        new ZookeeperSupport(zooKeeper,lockNodePath,0,0);
    }
    
    public Object retryOperation(ZookeeperOperations zookeeperOperations) throws InterruptedException, KeeperException {
         for(int c=0;c<this.retryCount;c++) {
             try {
                 return zookeeperOperations.execute();
             } catch (KeeperException.ConnectionLossException e) {
                 e.printStackTrace();
                 Thread.sleep(this.waitBeforeRetry);   
             }
         }   
         throw new RetryCountExceededException();
    }
    
    public String createLockPath(final String path) throws KeeperException, InterruptedException {
       return (String) this.retryOperation(new ZookeeperOperations(){

            public Object execute() throws KeeperException, InterruptedException {
                return zooKeeper.create(lockNodePath+"/"+path,new byte[0],aclList, CreateMode.EPHEMERAL_SEQUENTIAL);
            }
        });
    }


    public String createPath(String path) throws KeeperException, InterruptedException {
        return zooKeeper.create(path,new byte[0],aclList, CreateMode.PERSISTENT);
    }
    
    public boolean exists(final String name, final Watcher watcher) throws KeeperException, InterruptedException {
        return (Boolean) this.retryOperation(new ZookeeperOperations() {
            public Object execute() throws KeeperException, InterruptedException {
                boolean result=zooKeeper.exists(name,watcher)!=null;
                return result;
            }
        });
    }
    
    public void close() throws InterruptedException {
        this.zooKeeper.close();
    }
    
    
    class PreviousNodeWatcher implements Watcher{
        CountDownLatch countDownLatch;

        PreviousNodeWatcher(CountDownLatch countDownLatch){
            this.countDownLatch=countDownLatch;
        }
        
        public void process(WatchedEvent watchedEvent) {
            System.out.println("Notified "+watchedEvent.getType());
            if(watchedEvent.getType().equals(Event.EventType.NodeDeleted)){
                this.countDownLatch.countDown();
            }
        }
    }
    
    private boolean isExist(String node,Watcher watcher) throws KeeperException, InterruptedException {
       return zooKeeper.exists(node,watcher)!=null;
    }
    
    public  void setWatcherPreviousNode(String node) throws KeeperException, InterruptedException {
        CountDownLatch countDownLatch =new CountDownLatch(1);
        PreviousNodeWatcher previousNodeWatcher=new PreviousNodeWatcher(countDownLatch);
        String preNode=this.calculatePreNode(node)==null?null:node.substring(0,node.lastIndexOf('/'))+"/"+this.calculatePreNode(node);
        System.out.println("Lock for "+ node +" in on "+preNode);
        if(preNode !=null){
            try {
                if(isExist(preNode, previousNodeWatcher)){
                   try {
                       countDownLatch.await();
                   }catch(InterruptedException e){
                       e.printStackTrace();
                       setWatcherPreviousNode(node);
                    }
                }
            }catch (KeeperException.NoNodeException e){
                e.printStackTrace();
                setWatcherPreviousNode(node);
            }
        }
        
    }
    
    public String calculatePreNode(String name) throws KeeperException, InterruptedException {
        String preNode=null;
        TreeSet<String> childSet=this.getSortedChildrenName(this.lockNodePath);
        long nameSeq=Long.parseLong(name.substring(name.lastIndexOf('-')+1,name.length()));
        for(String child: childSet){
            long childSeq=Long.parseLong(child.substring(child.lastIndexOf('-')+1,child.length()));
            if(childSeq<nameSeq){
                preNode=child;
            }else{
                break;
            }
        }
        return preNode;
    } 
    
    public TreeSet<String> getSortedChildrenName(String path) throws KeeperException, InterruptedException {
        List<String> children=this.zooKeeper.getChildren(path,false);
        TreeSet<String> childSet=new TreeSet<String>();
        for(String name:children){
            childSet.add(name);
        }
        return childSet;
    }
    
    public void deleteNode(final String path) throws KeeperException, InterruptedException {
         this.retryOperation(new ZookeeperOperations() {
            public Object execute() throws KeeperException, InterruptedException {
                 zooKeeper.delete(path,-1);
                return null;
            }
        });
    }
    
}
