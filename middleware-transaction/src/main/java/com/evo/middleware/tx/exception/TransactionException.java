package com.evo.middleware.tx.exception;

import java.io.Serial;

public class TransactionException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public TransactionException() {
        super();
    }

    public TransactionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }

}
