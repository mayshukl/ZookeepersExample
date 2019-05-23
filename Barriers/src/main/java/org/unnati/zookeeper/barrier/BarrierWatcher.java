package org.unnati.zookeeper.barrier;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.Semaphore;

public class BarrierWatcher implements Watcher {
    
    private Semaphore semaphore;

    BarrierWatcher(Semaphore semaphore){
        this.semaphore=semaphore;
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while creating watcher",e);
        }
    }


    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType()== Event.EventType.NodeDeleted){
            semaphore.release();
        }
        
    }
}
