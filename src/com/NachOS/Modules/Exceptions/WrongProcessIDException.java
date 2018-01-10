package com.NachOS.Modules.Exceptions;

public class WrongProcessIDException extends IPCException {
    public WrongProcessIDException(String message){
        super(message);
    }
    public WrongProcessIDException(String message, Throwable throwable){
        super(message, throwable);
    }
}
