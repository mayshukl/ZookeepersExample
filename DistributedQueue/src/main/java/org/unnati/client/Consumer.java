package org.unnati.client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.unnati.Constants;
import org.unnati.zookeeper.queue.DistributedQueue;

import java.io.IOException;

public class Consumer implements Runnable {

    private DistributedQueue queue;
    private String name;

    public Consumer(String name) throws IOException, KeeperException, InterruptedException {
        this.name = name;
        ZooKeeper zooKeeper = new ZooKeeper(Constants.ZOOKEEPER_HOST + ":" + Constants.ZOOKEEPER_PORT, 10000, null);
        this.queue = new DistributedQueue(zooKeeper, Constants.QUEUE_NODE, null);

    }

    public void run() {
        while (true) {
            try {
                this.queue.poll();
                Thread.sleep(10000);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}   