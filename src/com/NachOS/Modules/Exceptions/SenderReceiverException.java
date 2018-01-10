package com.NachOS.Modules.Exceptions;

public class SenderReceiverException extends IPCException {
    public SenderReceiverException (String message){ super(message); }
    public SenderReceiverException(String message, Throwable throwable){ super(message, throwable);
    }
}
