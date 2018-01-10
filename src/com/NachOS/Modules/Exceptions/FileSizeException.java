package com.NachOS.Modules.Exceptions;

public class FileSizeException extends FileSystemException {
    public FileSizeException(String msg) {
        super(msg);
    }
    public FileSizeException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
