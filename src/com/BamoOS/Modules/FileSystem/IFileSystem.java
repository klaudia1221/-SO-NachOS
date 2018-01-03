package com.BamoOS.Modules.FileSystem;

import com.BamoOS.Modules.ACL.User;
import com.BamoOS.Modules.ProcessManager.IProcessManager;

public interface IFileSystem {
    FileBase getFileBase(String name);
    void openFile(String fileName) throws Exception;
    void closeFile(String name) throws Exception;
    void createFile(String fileName, User user, IProcessManager processManager) throws Exception;
    void appendFile(String fileName, String content) throws Exception;
    void deleteContent(String fileName) throws Exception;
    String readFile(String fileName) throws Exception;
    void deleteFile(String fileName) throws Exception;
    void renameFile(String oldName, String newName) throws Exception;
    String list();
    Catalog getCatalog();

}
