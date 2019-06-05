package org.unnati.zookeeper.txn;

public class ResourceFactory {

    public static XAResource getDSResource(){
        return new XAResourceImpl();
    }

    public static XAResource getJMSResource(){
        return new XAResourceImpl();
    }
}
