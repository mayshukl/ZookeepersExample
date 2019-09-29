package org.unnati.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.unnati.zookeeper.utils.ZookeeperSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import static org.unnati.Constants.LEADER_NODE;
import static org.unnati.Constants.ZOOKEEPER_HOST;
import static org.unnati.Constants.ZOOKEEPER_PORT;

public class DistributedProcess {

    static class LeaderWatcher implements Watcher{
        private  CountDownLatch latch;
        LeaderWatcher(CountDownLatch latch){
            this.latch=latch;
        }
        @Override
        public void process(WatchedEvent watchedEvent) {
            System.out.println("Root Node Watch");
            if(watchedEvent.getType().equals(Event.EventType.NodeDataChanged)){
                try {
                    String data=zookeeperSupport.getDataOnRoot(this);
                    if(leader!=null){
                        processes.remove(leader.getId());
                     //   leader.setIsLeader(false);
                    }
                    leader=processes.get(data);
                    leader.setIsLeader(true);
                    // Now there will be 2 leader , on will be in reachable network , old leader also. This situation is called split brain.

                    if(latch!=null) {
                        latch.countDown();
                    }

                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static volatile ZookeeperSupport zookeeperSupport;
    private static Map<String,Process> processes =new HashMap<String,Process>();
    private static CountDownLatch latch=new CountDownLatch(1);
    static {
        try {
            zookeeperSupport=new ZookeeperSupport(new ZooKeeper(ZOOKEEPER_HOST+":"+ZOOKEEPER_PORT,10000,null),LEADER_NODE);
            zookeeperSupport.createRootNode(new LeaderWatcher(latch));

            addProcess(new Process());
            addProcess(new Process());
            addProcess(new Process());
            addProcess(new Process());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }




    private static Process leader;


    public static Process getLeader(){

        if(processes.size()==0){
            throw new IllegalStateException("No Processes has been added");
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(leader==null){
            throw new NullPointerException();
        }
        return leader;
    }

    public static void addProcess(Process process){
        processes.put(process.getId(),process);
    }

    public static Process getProcess(){
        Process process=processes.get(random.nextInt(processes.size()-1)+1+"");
        while(process==null || process.isLeader()){
            int randomNumber=random.nextInt(processes.size())+1;
            process=processes.get(randomNumber+"");
        }
        return process;
    }

    static Random random=new Random();

}
