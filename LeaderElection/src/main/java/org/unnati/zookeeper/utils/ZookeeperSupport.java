package org.unnati.zookeeper.utils;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class ZookeeperSupport {
    private ZooKeeper zooKeeper;
    private String rootNode;
    List<ACL> aclList;

    public ZookeeperSupport(ZooKeeper zooKeeper, String rootNode, List<ACL> aclList){
        this.zooKeeper=zooKeeper;
        this.rootNode=rootNode;
        if (aclList==null){
            aclList= ZooDefs.Ids.OPEN_ACL_UNSAFE;
        }
        this.aclList=aclList;
    }

    public ZookeeperSupport(ZooKeeper zooKeeper, String rootNode){
        this(zooKeeper,rootNode,null);
    }

    public void createRootNode(Watcher watcher) throws KeeperException, InterruptedException {
        if(!this.isExist(this.rootNode,watcher)){
            this.zooKeeper.create(this.rootNode,new byte[0],this.aclList, CreateMode.PERSISTENT);
        }
    }


    public String createPath(String path) throws KeeperException, InterruptedException {
        return this.zooKeeper.create(this.rootNode+"/"+path,new byte[0],this.aclList, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    public void createPath(String path, AsyncCallback.StringCallback callback) throws KeeperException, InterruptedException {
        this.zooKeeper.create(this.rootNode+"/"+path,new byte[0],this.aclList, CreateMode.EPHEMERAL_SEQUENTIAL,callback,null);
    }

    public void delete(String path,boolean prefixRoot) throws KeeperException, InterruptedException {
        path= prefixRoot ? this.rootNode+"/"+path:path;
        this.zooKeeper.delete(path,-1);
    }




    public void registerWatcher(String path,String filter,boolean prefixRoot,Watcher watcher) throws KeeperException, InterruptedException {
        path= prefixRoot ? this.rootNode+"/"+path:path;
        String preNode=this.findMinChildren(path,filter);
        System.out.println(path +" will set the watcher on "+preNode);
        if(preNode==null){
            return;
        }
        this.isExist(this.rootNode+"/"+preNode,watcher);
    }


    public boolean isExist(String path, Watcher watcher) throws KeeperException, InterruptedException {
        boolean isExits= this.zooKeeper.exists(path,watcher)!=null;
        return isExits;
    }

    public String findMinChildren(String path,String filter) throws KeeperException, InterruptedException {
        TreeSet<String> treeSet = this.getSortedFilteredChildren(filter);
        long pathNumber=Long.parseLong(path.substring(path.lastIndexOf('-')+1,path.length()));
        String preWriteNode=null;
        for(String child:treeSet){
            long childNumber=Long.parseLong(child.substring(child.lastIndexOf('-')+1,child.length()));
            if(childNumber<pathNumber){
                preWriteNode=child;
            }else{
                break;
            }
        }
        return preWriteNode;
    }

    private TreeSet<String> getSortedFilteredChildren(String filter) throws KeeperException, InterruptedException {
        List<String> childList=this.zooKeeper.getChildren(this.rootNode,null);
        TreeSet<String> treeSet=new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                long sr1Number=Long.parseLong(str1.substring(str1.lastIndexOf('-')+1,str1.length()));
                long sr2Number=Long.parseLong(str2.substring(str2.lastIndexOf('-')+1,str2.length()));
                return sr1Number-sr2Number<0?-1:+1;
            }
        });
        for(String child:childList){
            if(filter!=null){
                if(child.contains(filter)) {
                    treeSet.add(child);
                }
            }else{
                treeSet.add(child);
            }
        }
        return treeSet;
    }

    public void setDataOnRoot(String data) throws KeeperException, InterruptedException {
        this.zooKeeper.setData(this.rootNode,data.getBytes(),-1);
    }

    public String getDataOnRoot() throws KeeperException, InterruptedException {
       return  new String(this.zooKeeper.getData(this.rootNode,false,new Stat()));
    }

    public String getDataOnRoot(Watcher watcher) throws KeeperException, InterruptedException {
        return  new String(this.zooKeeper.getData(this.rootNode,watcher,new Stat()));
    }
}
