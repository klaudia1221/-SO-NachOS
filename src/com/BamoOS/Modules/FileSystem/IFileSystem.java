package com.BamoOS.Modules.FileSystem;

import com.BamoOS.Modules.ACL.User;

public interface IFileSystem {
    FileBase getFileBase(String name);
    int openFile(String fileName);
    int closeFile(String name);
    int createFile(String fileName, User user);
    int appendFile(String fileName, String content);
    int deleteContent(String fileName);
    String readFile(String fileName);
    int deleteFile(String fileName);
    int renameFile(String oldName, String newName);
    String list();

}
