package com.NachOS.Modules.Exceptions;

public class FileNameException extends FileSystemException {
    public FileNameException(String msg) {
        super(msg);
    }
    public FileNameException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
