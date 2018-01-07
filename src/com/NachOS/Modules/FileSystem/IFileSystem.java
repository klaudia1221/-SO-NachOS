package com.NachOS.Modules.FileSystem;

import com.NachOS.Modules.ACL.User;
import com.NachOS.Modules.Exceptions.ChangedToWaitingException;
import com.NachOS.Modules.ProcessManager.IProcessManager;

public interface IFileSystem {
    FileBase getFileBase(String name) throws Exception;
    void openFile(String fileName) throws Exception;
    void closeFile(String name) throws Exception;
    void createFile(String fileName, User user, IProcessManager processManager) throws Exception;
    void appendFile(String fileName, String content) throws Exception;
    void deleteContent(String fileName) throws Exception;
    String readFile(String fileName) throws Exception;
    void deleteFile(String fileName) throws Exception;
    void renameFile(String oldName, String newName) throws Exception;
    File getFile(String fileName) throws Exception;
    String list();
    Catalog getCatalog();

}
