package org.unnati.clients;

import org.apache.zookeeper.ZooKeeper;
import org.unnati.Constants;
import org.unnati.zookeeper.ReadWriteLock;
import org.unnati.zookeeper.ReadWriteLockImpl;

import java.io.IOException;

public class ReadLockClient implements Runnable {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+ ": Starting");
        try {
            ReadWriteLock lock=new ReadWriteLockImpl(new ZooKeeper(Constants.ZOOKEEPER_HOST+":"+Constants.ZOOKEEPER_PORT,10000,null));

            System.out.println(Thread.currentThread().getName()+ ":Obtaining Read Lock");
            lock.readLock();
            System.out.println(Thread.currentThread().getName()+ ":Obtained Read Lock");
            try {
                System.out.println(Thread.currentThread().getName()+ ": Obtaining Read Lock");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+ ":Releasing Read Lock");
            lock.unlock();
            System.out.println(Thread.currentThread().getName()+ ": Released Read Lock");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
