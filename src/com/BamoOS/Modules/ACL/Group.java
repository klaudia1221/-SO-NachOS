package com.BamoOS.Modules.ACL;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private static int counter = 0;
    private int id;
    private String name;
    private List<User> users;

    public Group(String name){
        this.name = name;
        users = new ArrayList<>();
        id = counter;
        counter++;
    }
    public void addUser(User user) throws Exception {
        if(userExist(user)){
            throw new Exception("com.company.User exists.");
        }else{
            users.add(user);
        }
    }
    public void removeUser(User user) {
        if(users.remove(user)){
            System.out.println("User removed.");
        }else{
            System.out.println("Group dosen't contain user.");
        }
    }

    public boolean userExist(User user){
        for(User u : users){
            if(u.getName().equals(user.getName())){
                return true;
            }
        }
        return false;
    }
    public String getName(){
        return name;
    }
}
