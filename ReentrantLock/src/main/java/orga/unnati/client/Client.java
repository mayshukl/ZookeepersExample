package orga.unnati.client;

import org.apache.zookeeper.ZooKeeper;
import orga.unnati.Constants;
import orga.unnati.zookeeper.locks.DistributedReentrantLock;

import java.io.IOException;

public class Client implements Runnable {
    
    private ZooKeeper zooKeeper;

    public Client(){
        try {
            zooKeeper=new ZooKeeper(Constants.ZOOKEEPER_HOST + ":" + Constants.ZOOKEEPER_PORT, 10000, null);
        } catch (IOException e) {
            e.printStackTrace();
        };
    }
    
    public void run() {
        System.out.println(Thread.currentThread().getName()+" : Running");
        DistributedReentrantLock lock=new DistributedReentrantLock(this.zooKeeper,Constants.LOCK_NODE);
        System.out.println(Thread.currentThread().getName()+" : Trying to get Lock");
        lock.lock();
        System.out.println(Thread.currentThread().getName()+" : Processing");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName()+" : Unlocking");
        lock.unlock();
        System.out.println(Thread.currentThread().getName()+" : Unlocked");
    }
}
