package org.unnati.range;

import java.io.Serializable;

public final class Range implements Comparable, Serializable {
    private final Long start;
    private final Long end;
    private boolean isUsed;

    public Range(Long start,Long end){
        this.start=start;
        this.end=end;
    }

    public long getStartOfRange(){
        return this.start;
    }

    public long getEndOfRange(){
        return this.end;
    }

    public boolean isUsed(){
        return this.isUsed;
    }
    
    public void setIsUsed(){
        this.isUsed=true;
    }

    @Override
    public int compareTo(Object object) {
        if(! (object instanceof Range)){
            throw new IllegalArgumentException("Expected Class :"+this.getClass()+" but found "+object.getClass());
        }
        Range rangeToCompare=(Range)object;
        if((!this.isUsed)&&(rangeToCompare.isUsed)){
            return +1;
        }
        if((!rangeToCompare.isUsed)&&this.isUsed){
            return -1;
        }
        return (this.start-rangeToCompare.start)>0?+1:-1;
    }
}
