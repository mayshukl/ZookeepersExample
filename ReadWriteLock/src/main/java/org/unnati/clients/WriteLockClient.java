package org.unnati.clients;

import org.apache.zookeeper.ZooKeeper;
import org.unnati.Constants;
import org.unnati.zookeeper.ReadWriteLock;
import org.unnati.zookeeper.ReadWriteLockImpl;

import java.io.IOException;

public class WriteLockClient implements Runnable {
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+ ": Starting");
        try {
            ReadWriteLock lock=new ReadWriteLockImpl(new ZooKeeper(Constants.ZOOKEEPER_HOST+":"+Constants.ZOOKEEPER_PORT,10000,null));

            System.out.println(Thread.currentThread().getName()+ ": Obtaining Write Lock");
            lock.writeLock();
            System.out.println(Thread.currentThread().getName()+ ": Obtained Write Lock");
            try {
                System.out.println(Thread.currentThread().getName()+":  Write Lock");
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+ ": Releasing Write Lock");
            lock.unlock();
            System.out.println(Thread.currentThread().getName()+ ": Released Write Lock");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
