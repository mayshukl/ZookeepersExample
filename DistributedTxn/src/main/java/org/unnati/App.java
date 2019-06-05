package org.unnati;

import org.unnati.zookeeper.txn.ResourceFactory;
import org.unnati.zookeeper.txn.XAResource;
import org.unnati.zookeeper.txn.XATransaction;
import org.unnati.zookeeper.txn.XATransactionImpl;

public class App {

    public static void main(String args[]){
        XAResource ds= ResourceFactory.getDSResource();
        XAResource jms= ResourceFactory.getJMSResource();

        XATransaction transaction=new XATransactionImpl();
        transaction.beginTransaction();
        transaction.register(ds);
        transaction.register(jms);
        jms.execute();
        ds.execute();
        transaction.commit();

    }
}
