package com.freshworks.assignment.exception;

/**
 * @author Manikandan E
 */
public class DataStoreException extends RuntimeException {

    public DataStoreException(String message) {
        super(message);
    }

    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
