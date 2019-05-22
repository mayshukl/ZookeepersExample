package org.unnati.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.unnati.Constants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZookeeperProvider {
    
    private static String zkHost=Constants.ZOOKEEPER_HOST;
    private static String zkPort=Constants.ZOOKEEPER_PORT;
    private ZooKeeper zooKeeper;
    
    public ZookeeperProvider() throws IOException {
        this.zooKeeper=new ZooKeeper(zkHost+":"+zkPort,10000,new ZookeeperWatcher());
    }

    
    public boolean isNodeExist(String path){
       boolean isExist=false;
        try {
            Stat stat=this.zooKeeper.exists(path,false);
            isExist=stat!=null;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return isExist;
    }
    
    public  String addNode(String path,String data) throws InterruptedException, KeeperException {
        return this.zooKeeper.create(path,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
    public  String addNodeWithObject(String path,Object data)  {
        try {
            return this.zooKeeper.create(path,convertObjectToByteArray(data), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  void addData(String path,String data) throws InterruptedException, KeeperException {
         this.zooKeeper.setData(path,data.getBytes(),-1);
    }

    public  void addData(String path,Object data) throws InterruptedException, KeeperException {
        this.zooKeeper.setData(path,convertObjectToByteArray(data),-1);
    }

    public  Object getNodeDataAsObject(String path) throws InterruptedException, KeeperException {
        return convertByteArrayToObject(this.zooKeeper.getData(path,false,null));
    }
    
    public  String getRootNode() throws InterruptedException, KeeperException {
        return new String(this.zooKeeper.getData("/",false,null));
    }

    public  String getNodeData(String path) throws InterruptedException, KeeperException {
        return new String(this.zooKeeper.getData(path,false,null));
    }

    public List<String> getChildren(String path) throws InterruptedException, KeeperException {
        return this.zooKeeper.getChildren(path,false);
    }
    public Map<String,String> getChildrenData(String path) throws InterruptedException, KeeperException {
        Map<String,String> map=new HashMap<String, String>();     
        for(String childPath: this.zooKeeper.getChildren(path,false)){
            map.put(childPath,new String(this.zooKeeper.getData(childPath,false,null))) ;       
        }
        
        return map;
    }
    
    public void closeConnection(){
        try {
            this.zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private byte[] convertObjectToByteArray(Object object){
        byte[] byteObject=null;
        try(ByteArrayOutputStream bos=new ByteArrayOutputStream(); ObjectOutputStream oos=new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            byteObject=bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } return byteObject;
    }

    private Object convertByteArrayToObject(byte[] byteObject){
       Object object=null;
        try(ByteArrayInputStream bis=new ByteArrayInputStream(byteObject); ObjectInputStream ois=new ObjectInputStream(bis)) {
            object=ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }
    
}

