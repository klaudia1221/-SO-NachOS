package com.NachOS.Modules.Exceptions;

public class NoReceiverException extends Exception {
    public NoReceiverException(String message){
        super(message);
    }
    public NoReceiverException(String message, Throwable throwable){
        super(message, throwable);
    }
}
