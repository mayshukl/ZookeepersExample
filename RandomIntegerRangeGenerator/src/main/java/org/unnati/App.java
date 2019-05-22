package org.unnati;

import org.apache.zookeeper.KeeperException;
import org.unnati.client.Server;
import org.unnati.range.provider.ZookeeperRangeProvider;
import org.unnati.zookeeper.RangeZookeeperBootstrap;
import org.unnati.zookeeper.ZookeeperProvider;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class App {
    
    public static void main(String[] args) {
        bootstrapSetup();
        startClients();
    }
    
    private static void bootstrapSetup(){
        try {
            RangeZookeeperBootstrap.bootstrap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
    
    
    private static void startClients(){
        Executor executor= Executors.newCachedThreadPool();
        ((ThreadPoolExecutor)executor).setMaximumPoolSize(Constants.MAX_ZK_CLIENT_THREAD);
        
        for(int i=0;i<Constants.MAX_ZK_CLIENT_THREAD;i++){
            try {
                ((ThreadPoolExecutor) executor).submit(new Server(new ZookeeperRangeProvider(new ZookeeperProvider())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    private void testZookeeper() throws KeeperException, InterruptedException, IOException {
        ZookeeperProvider zookeeperProvider=new ZookeeperProvider();
        String node=zookeeperProvider.getRootNode();
        System.out.println("Node Data is "+node);
        System.out.println("Root Data children "+zookeeperProvider.getChildren("/"));
//        String newNode=zookeeperProvider.addNode("/customNode2","message=Hello from Zookeeper");
//        System.out.println("Root Data children "+zookeeperProvider.getNodeData(newNode));
//        zookeeperProvider.addData(newNode,"This is second Message");
//        System.out.println("Root Data children "+zookeeperProvider.getNodeData(newNode));
        System.out.println("Node Exist "+zookeeperProvider.isNodeExist("/URL_COUNTER"));
    }
}
