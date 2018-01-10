package com.NachOS.Modules.Exceptions;

public class FileSystemException extends Exception {
    public FileSystemException(String msg) {
        super(msg);
    }
    public FileSystemException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
