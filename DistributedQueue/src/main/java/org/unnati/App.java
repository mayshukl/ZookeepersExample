package org.unnati;

import org.apache.zookeeper.KeeperException;
import org.unnati.client.Consumer;
import org.unnati.client.Producer;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    
    public static AtomicInteger counter=new AtomicInteger(0);
    
    public static void main(String args[]) throws InterruptedException, IOException, KeeperException {

        init();
    }
    
    static void init() throws InterruptedException, IOException, KeeperException {
        ExecutorService executorService=Executors.newCachedThreadPool(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r,"Thread - "+counter.incrementAndGet());
            }
        });

        executorService.submit(new Producer("Producer 1"));
        executorService.submit(new Producer("Producer 2"));
        executorService.submit(new Producer("Producer 3"));
        executorService.submit(new Consumer("Consumer 1"));
        executorService.submit(new Consumer("Consumer 2"));
        
    }
    
}
