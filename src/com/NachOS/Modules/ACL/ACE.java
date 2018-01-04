package com.NachOS.Modules.ACL;


public class ACE {
    private User user;
    private Group group;
    private boolean isGroupAce;
    private Mask mask;

    public ACE(User user){
        this.user = user;
        group = null;
        isGroupAce = false;
    }
    public ACE(Group group){
        this.group = group;
        user = null;
        isGroupAce = true;
    }

    public boolean isGroupAce(){
        return isGroupAce;
    }
    public User getUser(){
        return user;
    }
    public Group getGroup(){
        return group;
    }
    public Mask getMask(){
        return mask;
    }
    public void setMask(Mask mask){
        this.mask = mask;
    }
}
