package com.BamoOS.Modules.ACL.Interfaces;

import com.BamoOS.Modules.ACL.*;
import com.BamoOS.Modules.FileSystem.FileBase;

public interface IACLController {
    void addAceForUser(User user, Mask mask, FileBase file);
    void addAceForGroup(Group group, Mask mask, FileBase file);
    boolean hasUserPremissionToOperation(FileBase file, User user, OperationType type);
    void setPremissionToDefaultGroup(FileBase file) throws Exception;
    void setPremissionToAdminGroup(FileBase file) throws Exception;
    void setDefaultPremissionToFile(FileBase file) throws Exception;
    void ShowACEForFfile(FileBase file);
}
