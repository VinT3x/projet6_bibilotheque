package com.oc.projet3.bibliows.exceptions;

public class WSException extends Exception {

    public WSException(String message, Throwable cause) {
        super(message, cause);
    }

    public WSException(String message) {
        super(message);
    }

}
