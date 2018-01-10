package com.NachOS.Modules.Exceptions;

public class InterpreterException extends Exception {
    public InterpreterException(String message){
        super(message);
    }
    public InterpreterException(String message, Throwable throwable){
        super(message, throwable);
    }
}
