package com.NachOS.Modules.FileSystem;

import com.NachOS.Modules.ACL.User;

public class FileBase {
    private static int COUNTER = 0;
    private int id;
    private User owner;

    public FileBase(User owner){
        this.owner = owner;
        id = COUNTER;
        COUNTER++;
    }

    public int getId(){
        return id;
    }

    public User getOwner() {
        return owner;
    }
}
