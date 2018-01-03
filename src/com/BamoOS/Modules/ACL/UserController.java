package com.BamoOS.Modules.ACL;

import com.BamoOS.Modules.ACL.Interfaces.IUserController;

import java.util.ArrayList;
import java.util.List;

public class UserController implements IUserController {
    private List<User> users;
    private List<Group> groups;

    public UserController() {
        users = new ArrayList<>();
        groups = new ArrayList<>();
        Group groupDefault = new Group("default");
        Group groupAdmin = new Group("admin");
        groups.add(groupDefault);
        groups.add(groupAdmin);
    }
    public UserController(List<User> users, List<Group> groups) {
        this.users = users;
        this.groups = groups;
        Group groupDefault = new Group("default");
        Group groupAdmin = new Group("admin");
        groups.add(groupDefault);
        groups.add(groupAdmin);
    }

    @Override
    public String showUserList() {
        StringBuilder builder = new StringBuilder();
        for(User user : users) {
            builder.append(user.getName());
            builder.append(" ");
        }
        return builder.toString();
    }

    @Override
    public void addUser(String name, String groupName) throws Exception {
        if(userExist(name)) {
            throw new Exception("User exists.");
        }else{
            if(groupName == null){
                User user = new User(name);
                users.add(user);
                addUserToDefaultGroup(user);
            }else{
                if(groupExist(groupName)){
                    User user = new User(name);
                    users.add(user);
                    addUserToGroup(user,groupName);
                }else{
                    User user = new User(name);
                    users.add(user);
                    addGroup(groupName);
                    addUserToGroup(user, groupName);
                }
            }
        }
    }
    private void addUserToDefaultGroup(User user){
        for (Group group : groups){
            if (group.getName().equals("default")){
                try {
                    group.addUser(user);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void addGroup(String groupName) throws Exception {
        if(groupExist(groupName)){
            throw new Exception("Group exists.");
        }
        Group group = new Group(groupName);
        groups.add(group);
    }
    @Override
    public void addUserToGroup(User user, String groupName) throws Exception {
        if(!userExist(user.getName())){
            throw new Exception("User dosen't exist.");
        }
        if(!groupExist(groupName)){
            throw new Exception("Group dosen't exist.");
        }
        for(Group group : groups){
            if(group.getName().equals(groupName)){
                group.addUser(user);
                break;
            }
        }
    }

    @Override
    public void removeUserFromAllGroups(User user) {
        for(Group group : groups){
            group.removeUser(user);
        }
    }
    @Override
    public void removeUserFromGroup(String name, String groupName) throws Exception {
        if(!userExist(name)){
            throw new Exception("User dosen't exist.");
        }
        if(!groupExist(groupName)){
            throw new Exception("Group dosen't exist.");
        }
        for(Group group : groups){
            if(group.getName().equals(groupName)){
                group.removeUser(getUser(name));
                break;
            }
        }
    }

    @Override
    public User getUser(String name) throws Exception {
        for (User user : users){
            if(user.getName().equals(name)){
                return user;
            }
        }
        throw new Exception("User dosen't exist.");
    }

    @Override
    public Group getGroup(String groupName) throws Exception {
        for(Group group : groups){
            if(group.getName().equals(groupName)){
                return group;
            }
        }
        throw new Exception("Group doesn't exist.");
    }
    public List<Group> getUserGroups(String userName) throws Exception{
        User user = getUser(userName);
        List<Group> userGroups = new ArrayList<>();
        for(Group group : groups){
            if(group.userExist(user)){
                userGroups.add(group);
            }
        }
        return userGroups;
    }
    public String printUserGroups(String userName) throws Exception{
        List<Group> userGroups = getUserGroups(userName);
        String result = "";
        for(Group group : userGroups){
            result += group.getName();
            result += " ";
        }
        return result;
    }
    @Override
    public void removeUser(String name) throws Exception {
        removeUserFromAllGroups(getUser(name));
        users.remove(getUser(name));
    }

    @Override
    public String printGroups() {
        String result = "";
        for(Group group : groups){
            result += group.getName();
            result += " ";
        }
        return result;
    }

    @Override
    public List<Group> getGroups() {
        return groups;
    }


    public boolean userExist(String name) {
        for(User u : users) {
            if(u.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    public boolean groupExist(String groupName){
        for(Group group : groups){
            if(group.getName().equals(groupName)){
                return true;
            }
        }
        return false;
    }
}
