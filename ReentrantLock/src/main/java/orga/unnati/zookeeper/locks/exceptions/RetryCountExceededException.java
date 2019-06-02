package orga.unnati.zookeeper.locks.exceptions;

public class RetryCountExceededException extends RuntimeException {

    public RetryCountExceededException(){
        super("Maximum Number of retry has been exceeded");
    }
}
