package org.unnati;

import org.unnati.client.Client;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class Apps {

    public static void main(String args[]){
        init();
    }


    static void init(){
         final AtomicInteger count= new AtomicInteger(1);
        ThreadPoolExecutor threadPoolExecutor= (ThreadPoolExecutor) Executors.newCachedThreadPool();
        threadPoolExecutor.setThreadFactory(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"Thread "+ count.getAndIncrement());
            }
        });


        threadPoolExecutor.execute(new Client());
        threadPoolExecutor.execute(new Client());
        threadPoolExecutor.execute(new Client());
        threadPoolExecutor.execute(new Client());
        threadPoolExecutor.execute(new Client());
        try {
            System.out.println("Going to sleep");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("Going to sleep");
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Woke up");
        threadPoolExecutor.execute(new Client());

    }
}
