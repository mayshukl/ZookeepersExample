package orga.unnati;

import orga.unnati.client.Client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class App {
    static AtomicInteger integer=new AtomicInteger();
    public static void main(String[] args){
        init();
    }
    
    static void init(){
        
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executorService.setThreadFactory(new ThreadFactory() {
            public Thread newThread(Runnable r) {
                return new Thread(r,"Thread "+integer.getAndIncrement());
            }
        });
    
        System.out.println("String");

        executorService.submit(new Client());
        executorService.submit(new Client());
        executorService.submit(new Client());
        executorService.submit(new Client());
        executorService.submit(new Client());
        
        
    }
}
