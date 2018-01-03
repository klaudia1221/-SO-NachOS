package com.BamoOS.Modules.ACL;

public class User {
    private static int counter = 0;
    private int id;
    private String name;
    private boolean isLogged;

    public User(String name){
        this.name = name;
        id = counter;
        counter++;
    }

    public String getName(){
        return name;
    }
    public boolean isLogged(){
        return isLogged;
    }
    public void setLogged(boolean logged){
        this.isLogged = logged;
    }
    @Override
    public boolean equals(Object obj){
        if(obj == null) return false;
        User user = (User) obj;
        if(user.getName().equals(getName())){
            return true;
        }else{
            return false;
        }
    }
}
