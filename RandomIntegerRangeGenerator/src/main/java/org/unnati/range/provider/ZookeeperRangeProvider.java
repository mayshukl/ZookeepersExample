package org.unnati.range.provider;

import org.apache.zookeeper.KeeperException;
import org.unnati.Constants;
import org.unnati.range.Range;
import org.unnati.zookeeper.ZookeeperProvider;

import java.util.Map;

public class ZookeeperRangeProvider implements RangeProvider {

    private ZookeeperProvider zookeeperProvider;

    public ZookeeperRangeProvider(ZookeeperProvider zookeeperProvider) {
        this.zookeeperProvider = zookeeperProvider;
    }

    public Range getNextRange() {
        try {
            String currentRange = this.zookeeperProvider.getNodeData(Constants.ZOOKEEPER_CURRENT_RANGE);
            Map<Long, Range> ranges = (Map<Long, Range>) this.zookeeperProvider.getNodeDataAsObject(Constants.ZOOKEEPER_DATA_NODE);
            Range range = ranges.get(Long.parseLong(currentRange));
            range.setIsUsed();
            this.zookeeperProvider.addData(Constants.ZOOKEEPER_CURRENT_RANGE, ((Long) (range.getEndOfRange() + 1)).toString());
            return range;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Not in expected state");
    }

}
