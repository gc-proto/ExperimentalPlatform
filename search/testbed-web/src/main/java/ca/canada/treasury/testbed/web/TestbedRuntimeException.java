package ca.canada.treasury.testbed.web;

public class TestbedRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TestbedRuntimeException() {
        super();
    }
    public TestbedRuntimeException(String message, Throwable cause,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    public TestbedRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
    public TestbedRuntimeException(String message) {
        super(message);
    }
    public TestbedRuntimeException(Throwable cause) {
        super(cause);
    }
}
