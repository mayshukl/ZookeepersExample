package org.unnati.client;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.unnati.zookeeper.barrier.Barrier;

public class ZkClient implements Runnable{

    Barrier barrier;
    public ZkClient(Barrier barrier){
        this.barrier=barrier;
    }

    @Override
    public void run() {
        System.out.println("Started");
        try {
            this.barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Wait Completed");
    }
}
