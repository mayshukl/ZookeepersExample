package org.unnati;

import org.unnati.client.ZkClient;
import org.unnati.zookeeper.barrier.Barrier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class App {

    public static void main(String args[]) {
        System.out.println("Starting Main");
        createBarrier();
        Executor executor = Executors.newSingleThreadExecutor();
        List<Future> futures = initClient(executor);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        deleteBarrier();
        Future future = futures.get(0);
        while (!future.isDone()) ;
        ((ExecutorService) executor).shutdown();
        System.out.println("Ending Main");
    }

    private static void createBarrier() {
        Barrier barrier = new Barrier();
        try {
            barrier.createBarrierNode();
            barrier.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static List<Future> initClient(Executor executor) {
        List<Future> futures = new ArrayList<>();
        Barrier barrier = new Barrier();
        
        futures.add(((ExecutorService) executor).submit(new ZkClient(barrier)));
        return futures;
    }


    private static void deleteBarrier() {
        Barrier barrier = new Barrier();
        try {
            barrier.deleteBarrierNode();
            barrier.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
