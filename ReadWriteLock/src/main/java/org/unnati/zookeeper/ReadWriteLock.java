package org.unnati.zookeeper;

public interface ReadWriteLock {
    public void readLock();
    public void writeLock();
    public void unlock();
}
