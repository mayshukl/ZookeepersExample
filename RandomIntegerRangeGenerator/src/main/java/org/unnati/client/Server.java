package org.unnati.client;

import org.unnati.range.Range;
import org.unnati.range.provider.RangeProvider;

public class Server implements Runnable {
    RangeProvider rangeProvider;
    public Server(RangeProvider rangeProvider){
        this.rangeProvider=rangeProvider;
    }
   
    @Override
    public void run() {
        System.out.println("started");
        Range range=rangeProvider.getNextRange();
        System.out.println("Range : "+range.getStartOfRange() +" - "+range.getEndOfRange());
    }
}
