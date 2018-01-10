package com.NachOS.Modules.FileSystem;

import com.NachOS.Modules.ACL.User;
import com.NachOS.Modules.Exceptions.ChangedToWaitingException;
import com.NachOS.Modules.ProcessManager.IProcessManager;
import com.NachOS.Modules.Exceptions.*;

public interface IFileSystem {
    FileBase getFileBase(String name) throws FileNameException;
    void openFile(String fileName) throws FileNameException, ChangedToWaitingException;
    void closeFile(String name) throws FileNameException, FileNotOpenException;
    void createFile(String fileName, User user, IProcessManager processManager) throws FileNameException, FileSizeException;
    void appendFile(String fileName, String content) throws FileNameException, FileNotOpenException, FileSizeException;
    void deleteContent(String fileName) throws FileNameException, FileNotOpenException;
    String readFile(String fileName) throws FileNameException, FileNotOpenException;
    String readFileShell(String fileName) throws FileNameException;
    void deleteFile(String fileName) throws FileNameException;
    void renameFile(String oldName, String newName) throws FileNameException;
    File getFile(String fileName) throws FileNameException;
    String list();
    Catalog getCatalog();

}
