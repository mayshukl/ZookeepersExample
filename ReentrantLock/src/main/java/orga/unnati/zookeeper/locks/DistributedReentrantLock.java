package orga.unnati.zookeeper.locks;

import com.sun.istack.internal.NotNull;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import orga.unnati.zookeeper.locks.utils.ZookeeperSupport;

public class DistributedReentrantLock implements Lock {
    
    private ZookeeperSupport zookeeperSupport;
    private String lockNodePath;
    private String lockPrefix="guid-lock-";
    private String path;
    
    public DistributedReentrantLock(ZooKeeper zooKeeper, @NotNull String lockNode){
        this.zookeeperSupport=new ZookeeperSupport(zooKeeper,lockNode,0,0);
        this.lockNodePath=lockNode;
        try {
            if(!this.zookeeperSupport.exists(lockNode,null)){
                try {
                    this.zookeeperSupport.createPath(lockNode);
                }catch (KeeperException.NodeExistsException e){
                    //
                }
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
    public void lock() {
        try {
             this.path=this.zookeeperSupport.createLockPath(this.lockPrefix);
            this.zookeeperSupport.setWatcherPreviousNode(path);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void unlock() {
        try {
            this.zookeeperSupport.deleteNode(this.path);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
