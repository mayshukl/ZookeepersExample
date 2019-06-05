package org.unnati.client;

import org.unnati.zookeeper.DistributedProcess;
import org.unnati.zookeeper.Process;

public class Client implements Runnable {
    @Override
    public void run() {
        Process process=DistributedProcess.getProcess();

        process.read();
        process.read();
        process.write();
        process.write();

    }
}
