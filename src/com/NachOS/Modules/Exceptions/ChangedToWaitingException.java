package com.NachOS.Modules.Exceptions;

public class ChangedToWaitingException extends Exception {
    public ChangedToWaitingException(String message){
        super(message);
    }
    public ChangedToWaitingException(String message, Throwable throwable){
        super(message, throwable);
    }
}

