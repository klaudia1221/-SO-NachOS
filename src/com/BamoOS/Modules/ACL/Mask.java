package com.BamoOS.Modules.ACL;

public class Mask {
    private boolean read;
    private boolean write;
    private boolean execute;

    public Mask(boolean read, boolean write, boolean execute) {
        this.read = read;
        this.write = write;
        this.execute = execute;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isWrite() {
        return write;
    }

    public void setWrite(boolean write) {
        this.write = write;
    }

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }
}
