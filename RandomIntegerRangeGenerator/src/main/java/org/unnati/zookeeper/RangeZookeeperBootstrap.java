package org.unnati.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.unnati.Constants;
import org.unnati.range.Range;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RangeZookeeperBootstrap {
    /**
     * This function will populate 10000 records having range of 1M  
     * */
    
    public static void bootstrap() throws IOException, KeeperException, InterruptedException {
            
            Map<Long,Range> ranges=new HashMap<>();
            for(long i=0;i<100000001L;i=i+100001L){
                ranges.put(i,new Range(i,i+100000L));
            }
            ZookeeperProvider zookeeperProvider=new ZookeeperProvider();
            if(!zookeeperProvider.isNodeExist(Constants.ZOOKEEPER_DATA_NODE)){
                zookeeperProvider.addNodeWithObject(Constants.ZOOKEEPER_DATA_NODE,ranges);
                zookeeperProvider.addNode(Constants.ZOOKEEPER_CURRENT_RANGE,"0");
            }
            
    }
}
