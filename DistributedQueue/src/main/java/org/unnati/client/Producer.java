package org.unnati.client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.unnati.Constants;
import org.unnati.zookeeper.queue.DistributedQueue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class Producer implements Runnable {
    private static AtomicInteger counter=new AtomicInteger(0);
    private DistributedQueue queue;
    private String name;
    
    public Producer(String name) throws IOException, KeeperException, InterruptedException {
        this.name="Producer : "+name;
        ZooKeeper zooKeeper=new ZooKeeper(Constants.ZOOKEEPER_HOST+":"+Constants.ZOOKEEPER_PORT,10000,null);
        this.queue=new DistributedQueue(zooKeeper,Constants.QUEUE_NODE,null);
        
    }
    
    public void run() {
        while (true){
            try {
                this.queue.offer(new String(""+counter.incrementAndGet()).getBytes());
                Thread.sleep(10000);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
