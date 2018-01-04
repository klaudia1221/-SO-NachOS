package com.NachOS.Modules.ACL.Interfaces;

import com.NachOS.Modules.FileSystem.FileBase;
import com.NachOS.Modules.ACL.Group;
import com.NachOS.Modules.ACL.Mask;
import com.NachOS.Modules.ACL.OperationType;
import com.NachOS.Modules.ACL.User;

public interface IACLController {
    void addAceForUser(User user, Mask mask, FileBase file);
    void addAceForGroup(Group group, Mask mask, FileBase file);
    boolean hasUserPremissionToOperation(FileBase file, User user, OperationType type);
    void setPremissionToDefaultGroup(FileBase file) throws Exception;
    void setPremissionToAdminGroup(FileBase file) throws Exception;
    void setDefaultPremissionToFile(FileBase file) throws Exception;
    void ShowACEForFfile(FileBase file);
}
