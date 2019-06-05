package org.unnati.zookeeper.txn;

public interface XATransaction {
    public void beginTransaction();
    public void register(XAResource xaResource);
    public void commit();
    public void rollBack();
}
