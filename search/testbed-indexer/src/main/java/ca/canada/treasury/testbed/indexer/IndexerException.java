package ca.canada.treasury.testbed.indexer;

/**
 * Problem during indexer execution.
 * @author Pascal Essiembre
 */
public class IndexerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IndexerException() {
    }
    public IndexerException(String message) {
        super(message);
    }
    public IndexerException(Throwable cause) {
        super(cause);
    }
    public IndexerException(String message, Throwable cause) {
        super(message, cause);
    }
    public IndexerException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
