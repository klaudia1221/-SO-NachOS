package com.NachOS.Modules.Exceptions;

public class UndefinedOrderException extends InterpreterException{
    public UndefinedOrderException(String message){
        super(message);
    }
    public UndefinedOrderException(String message, Throwable throwable){
        super(message, throwable);
    }
}
