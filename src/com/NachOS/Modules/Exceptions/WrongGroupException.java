package com.NachOS.Modules.Exceptions;

public class WrongGroupException extends IPCException {
    public WrongGroupException(String message){ super(message); }
    public WrongGroupException(String message, Throwable throwable){
        super(message, throwable);
    }
}
