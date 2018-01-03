package com.BamoOS.Modules.ACL.Interfaces;

import com.BamoOS.Modules.ACL.Group;
import com.BamoOS.Modules.ACL.User;

import java.util.List;

public interface IUserController {
    String showUserList();
    void addUser(String name, String groupName) throws Exception;
    void addUserToGroup(User user, String groupName) throws Exception;
    User getUser(String name) throws Exception;
    void removeUser(String name) throws Exception;
    String printGroups();
    List<Group> getGroups();
    List<Group> getUserGroups(String userName) throws Exception;
    void addGroup(String groupName) throws Exception;
    void removeUserFromAllGroups(User user);
    Group getGroup(String groupName) throws Exception;
    void removeUserFromGroup(String name, String groupName) throws Exception;
}
