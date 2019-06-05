package org.unnati.zookeeper.txn;

import com.sun.istack.internal.Nullable;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.unnati.Constants;
import org.unnati.zookeeper.txn.utils.ZooKeeperSupport;

import java.io.IOException;

public class XAResourceImpl implements XAResource {

    private ZooKeeperSupport zooKeeperSupport;
    private String node;
    public  XAResourceImpl(){
        try {
            this.zooKeeperSupport=new ZooKeeperSupport(new ZooKeeper(Constants.ZOOKEEPER_HOST+":"+Constants.ZOOKEEPER_PORT,10000,null),null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void participate(XATransaction xaTransaction) {
        xaTransaction.register(this);
    }
    public void registerWatch(String node, Watcher watcher) {
        try {
            this.node=this.zooKeeperSupport.addEphemeralNode(node,false);
            this.zooKeeperSupport.getData(node,watcher,true);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void commit() {
        System.out.println(Thread.currentThread().getName()+"Committing");
        try {
            this.zooKeeperSupport.delete(this.node);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void rollback() {
        System.out.println(Thread.currentThread().getName()+"Rollbacking");
        try {
            this.zooKeeperSupport.delete(this.node);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void execute() {
      Thread t= new Thread(new Runnable() {
           public void run() {
               System.out.println(Thread.currentThread().getName()+"Executing");
               try {
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }, "Resource");

      t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void prepare() {
        System.out.println(Thread.currentThread().getName()+"Preparing");
        Double random=Math.random();
        String result;
        if(random%2==0){
            result="COMMIT";
        }else{
            result="ROLLBACK";
        }
        try {
            this.zooKeeperSupport.setData(this.node,result,false);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
