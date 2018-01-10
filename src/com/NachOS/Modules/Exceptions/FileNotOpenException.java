package com.NachOS.Modules.Exceptions;

public class FileNotOpenException extends FileSystemException {
    public FileNotOpenException(String msg) {
        super(msg);
    }
    public FileNotOpenException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
