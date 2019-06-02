package org.unnati;

import org.unnati.clients.ReadLockClient;
import org.unnati.clients.WriteLockClient;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Apps {
    
    static class ExceptionLoggerThreadPoolExecutor extends ThreadPoolExecutor {

        public ExceptionLoggerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);

        }

        public void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            if (t != null) {
                t.printStackTrace();
            }
        }
    }
    public static void main(String[] args){
        init();
    }
    static int counter=1;
    public static void init(){
        
       // ExecutorService executorService= new ExceptionLoggerThreadPoolExecutor( 10, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));
        ExecutorService executorService=Executors.newCachedThreadPool();
        ((ThreadPoolExecutor)executorService).setThreadFactory(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread=new Thread(r,"Thread "+ counter++);
                thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        e.printStackTrace();
                    }
                });
                return thread;
            }
        });
        executorService.execute(new ReadLockClient());
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.execute(new WriteLockClient());
        executorService.execute(new WriteLockClient());
        
        executorService.execute(new ReadLockClient());
        executorService.execute(new ReadLockClient());
        executorService.execute(new ReadLockClient());
    }
}
