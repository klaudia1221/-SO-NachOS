package com.BamoOS.Modules.FileSystem;

import com.BamoOS.Modules.ACL.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Catalog extends FileBase {
    private List<FileBase> root;

    public void add(File file){
        this.root.add(file);
    }

    public int size() { return root.size(); }

    public Catalog(User user) {
        super(user);
        root = new ArrayList<FileBase>();
    }

    public File get(int i) { return (File) root.get(i);}

    public File getFileByName(String name) {
        File tempFile = null;
        for (FileBase fileBase : root){
            File file = (File) fileBase;
            if(file.FILE_NAME.equals(name)){ tempFile = file; }
        }
        return tempFile;
    }

    public int getFirstBlock(String fileName) { return getFileByName(fileName).FIRST_BLOCK; }

    public int getLastBlock(String fileName) { return getFileByName(fileName).LAST_BLOCK; }

    public void setLastBlock(String fileName, int n) {
        for(FileBase file : root){
            File tempFile = (File) file;
            if((tempFile.FILE_NAME.equals(fileName))){ tempFile.LAST_BLOCK=n; }
        }
    }

    public void updateFileContent(String fileName, String content) {
        for(FileBase file : root){
            File tempFile = (File) file;
            if(tempFile.FILE_NAME.equals(fileName)){ tempFile.open(content); }
        }
    }

    public void close_file(String fileName) {
        for(FileBase file : root){
            File tempFile = (File) file;
            if(tempFile.FILE_NAME.equals(fileName)){ tempFile.close(); }
        }
    }

    public int getSize(String fileName) {
        int size = 0;
        for (FileBase file : root){
            File tempFile = (File) file;
            if(tempFile.FILE_NAME.equals(fileName)){ size=tempFile.FILE_SIZE; }
        }
        return size;
    }
    void incBlocksNum(String fileName) {
        for (FileBase file : root) {
            File tempFile = (File) file;
            if (tempFile.FILE_NAME.equals(fileName)) { tempFile.BLOCK_NUM++; }
        }
    }

    public void changeName(String oldName, String newName){
        for(FileBase file : root){
            File tempFile = (File) file;
            if(tempFile.FILE_NAME.equals(oldName)){ tempFile.FILE_NAME=newName; }
        }
    }

    public void changeSize(String fileName, int size) {
        for(FileBase file : root){
            File tempFile = (File) file;
            if(tempFile.FILE_NAME.equals(fileName)){ tempFile.setSize(size); }
        }
    }

    public void changeLast(String fileName, int block) {
        for(FileBase file : root){
            File tempFile = (File) file;
            if(tempFile.FILE_NAME.equals(fileName)){ tempFile.setLast(block); }
        }
    }

    public void deleteFile(String fileName) {
        Iterator<FileBase> it = root.iterator();
        while(it.hasNext()) {
            FileBase value = it.next();
            File valueTemp = (File) value;
            if ((valueTemp.FILE_NAME.equals(fileName))) {
                it.remove();
                break;
            }
        }
    }

    public boolean open_check(String fileName) {
        for(FileBase file : root){
            File tempFile = (File) file;
            if(tempFile.FILE_NAME.equals(fileName)){
                if (!tempFile.opened) { return false; }
                else { return true; }
            }
        }
        return false;
    }

    public String getContent(String fileName) {
        String res = new String();
        for(FileBase file : root){
            File tempFile = (File) file;
            if(tempFile.FILE_NAME.equals(fileName)) { res = tempFile.opened_file; }
        }
        return res;
    }
}
