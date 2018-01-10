package com.NachOS.Modules.Exceptions;

public class TooLongException extends IPCException {
    public TooLongException(String message){
        super(message);
    }
    public TooLongException(String message, Throwable throwable){
        super(message, throwable);
    }
}
