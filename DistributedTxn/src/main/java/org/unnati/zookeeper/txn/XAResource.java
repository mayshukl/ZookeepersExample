package org.unnati.zookeeper.txn;

import org.apache.zookeeper.Watcher;

public interface XAResource {
    public void participate(XATransaction xaTransaction);
    public void registerWatch(String node,Watcher watcher);
    public void commit();
    public void rollback();
    public void execute();
    public void prepare();
}
