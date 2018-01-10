package com.NachOS.Modules.Exceptions;

public class IPCException extends Exception {
    public IPCException(String message){
        super(message);
    }
    public IPCException(String message, Throwable throwable){
        super(message, throwable);
    }
}
