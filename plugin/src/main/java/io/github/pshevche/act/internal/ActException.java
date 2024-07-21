package io.github.pshevche.act.internal;

public class ActException extends RuntimeException {

    public ActException(String message) {
        super(message);
    }

    public ActException(String message, Exception cause) {
        super(message, cause);
    }

    public ActException(Exception e) {
        super(e);
    }
}
