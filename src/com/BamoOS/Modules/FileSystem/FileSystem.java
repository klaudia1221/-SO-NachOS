package com.BamoOS.Modules.FileSystem;

import com.BamoOS.Modules.ACL.User;
import com.BamoOS.Modules.ProcessManager.IProcessManager;

public class FileSystem implements IFileSystem {
    DiscDrive Drive = new DiscDrive();      //Dysk
    private Catalog dir;    //Katalog domyslny, w ktorym zapisywane sa wszystkie wpisy - obiekty File
    //Operacje na dysku
    private IProcessManager processManager;

    public FileSystem(Catalog catalog, IProcessManager processManager){
        this.dir = catalog;
        this.processManager = processManager;
    }

    public FileBase getFileBase(String name) throws Exception{
        if(!nameExists(name)){
            throw new Exception("Name dosen't exist.");
        }
        return dir.getFileByName(name);
    }

    public void openFile(String fileName) throws Exception {
        if (!nameExists(fileName)) { throw new Exception("Plik o takiej nazwie nie istnieje."); }
        else {
            dir.getFileByName(fileName).cv.await(false);
            String tmp = new String();
            int block = dir.getFirstBlock(fileName), i=0;
            while (tmp.length()<=dir.getSize(fileName)) {
                if (i==31) {
                    block = Drive.lastByte(block);
                    i=0;
                }
                tmp += Drive.getAt(i+block*32);
                i++;
            }
            dir.updateFileContent(fileName, tmp);
        }
    }

    public void closeFile(String fileName) throws Exception {
        if (!nameExists(fileName)) { throw new Exception("Plik o takiej nazwie nie istnieje."); }
        else if (!dir.open_check(fileName)) { throw new Exception("Plik o takiej nazwie nie jest otwarty."); }
        else {
            dir.close_file(fileName);
            dir.getFileByName(fileName).cv.signal();
        }
    }

    public void createFile(String fileName, User user, IProcessManager processManager) throws Exception{
        if (nameExists(fileName)) { throw new Exception("Plik o takiej nazwie istnieje.");}
        else if (Drive.FREE_BLOCKS==0) { throw new Exception("Za mało miejsca na dysku."); }
        else {
            int index = firstFreeBlock();
            Drive.bitVec[index] = false;
            Drive.FREE_BLOCKS--;
            Drive.putByte((char) 32 , (index+1) *32 - 1);
            dir.add(new File(fileName, index, user, processManager));
        }
    }

    public void appendFile(String fileName, String content) throws Exception {
        if (!nameExists(fileName)) { throw new Exception("Plik o takiej nazwie nie istnieje."); }
        else if (!dir.open_check(fileName)) { throw new Exception("Plik o takiej nazwie nie jest otwarty."); }
        else if (((double)content.length()/31.0)>=Drive.FREE_BLOCKS) { throw new Exception("Za mało miejsca na dysku."); }
        else {
            dir.updateFileContent(fileName, content);
            int current_block, i;
            if (dir.getFileByName(fileName).FILE_SIZE==0) {
                current_block = dir.getFirstBlock(fileName);
                i = 0;
            }
            else {
                current_block = dir.getLastBlock(fileName);
                if (dir.getFileByName(fileName).FILE_SIZE%31==0) {
                    dir.incBlocksNum(fileName);
                    Drive.putByte((char) firstFreeBlock(), (current_block+1) * 32 - 1);
                    current_block = firstFreeBlock();
                    Drive.bitVec[current_block]=false;
                    Drive.FREE_BLOCKS--;
                }
                i = dir.getFileByName(fileName).FILE_SIZE%31;

            }
            dir.getFileByName(fileName).FILE_SIZE += content.length();
            while (content.length()!=0) {
                if (i==31) {
                    Drive.putByte((char) firstFreeBlock(), (current_block+1) * 32 - 1);
                    current_block=firstFreeBlock();
                    Drive.bitVec[current_block]=false;
                    Drive.FREE_BLOCKS--;
                    dir.incBlocksNum(fileName);
                    i=0;
                }
                Drive.putByte(getChar(content), (i + ((current_block * 32))));
                content=removeChar(content);
                if (content.length()==0) { dir.setLastBlock(fileName, current_block); Drive.putByte((char) 32 , (current_block+1) * 32 - 1); }
                i++;
            }
        }
            }

    public void deleteContent(String fileName) throws Exception {
        if (!nameExists(fileName)) {throw new Exception("Plik o takiej nazwie nie istnieje."); }
        else if (!dir.open_check(fileName)) { throw new Exception("Plik o takiej nazwie nie jest otwarty.");  }
        else {
            int block = dir.getFirstBlock(fileName);
            dir.changeLast(fileName, block);
            dir.changeSize(fileName, 0);
            for (int i = block*32; i<block*32 + 31; i++) {
                Drive.putByte((char) 0, i);
            }
            block = Drive.lastByte(block);
            while (block != 32) {
                Drive.bitVec[block] = true;
                block = Drive.lastByte(block);
            }
        }
    }

    public String readFile(String fileName) throws Exception {
        if (!nameExists(fileName)) { throw new Exception("Plik o takiej nazwie nie istnieje."); }
        else if (!dir.open_check(fileName)) { throw new Exception("Plik o takiej nazwie nie jest otwarty."); }
        else { return dir.getContent(fileName); }
    }

    public void deleteFile(String fileName) throws Exception {
        if (!nameExists(fileName)) { throw new Exception("Plik o takiej nazwie nie istnieje.");  }
        else {
            int block = dir.getFirstBlock(fileName);
            while (block != 32){
                Drive.bitVec[block] = true;
                block = Drive.lastByte(block);
            }
            Drive.FREE_BLOCKS += dir.getFileByName(fileName).BLOCK_NUM;
            dir.deleteFile(fileName);
        }
    }

    public void renameFile(String oldName, String newName) throws Exception {
        if (!nameExists(oldName)) { throw new Exception("Plik o takiej nazwie nie istnieje."); }
        if (nameExists(newName)) { throw new Exception("Plik o takiej nazwie już istnieje."); }
        else { dir.changeName(oldName, newName); }
    }

    public String list() {
        String dir_list = new String();
        for (int i=0; i<dir.size(); i++) {
            dir_list=dir_list + dir.get(i).FILE_NAME + " " + dir.get(i).FILE_SIZE + " " + dir.get(i).FIRST_BLOCK + " " + dir.get(i).LAST_BLOCK + "\n";
        }
        return dir_list;
    }

    public File getFile(String fileName)throws Exception{
        if(!nameExists(fileName)){
            throw new Exception("Plik o takiej nazwie nie istnieje.");
        }else {
            return dir.getFileByName(fileName);
        }
    }

    //Metody pomocnicze

    private boolean nameExists(String name){
        boolean result = false;
        for (int i=0; i<dir.size(); i++){
            if (dir.get(i).FILE_NAME.equals(name)){
                result = true;
            }
        }
        return result;
    }

    private int firstFreeBlock(){
        int index=33;
        for (int i=0; i<Drive.BLOCKS_AMOUNT; i++){
            if (Drive.bitVec[i]){
                index=i;
                break;
            }
        }
        return index;
    }

    private static char getChar(String str){
        return str.charAt(0);
    }

    private static String removeChar(String s) {
        StringBuilder build = new StringBuilder(s);
        build.deleteCharAt(0);
        return build.toString();
    }
    public Catalog getCatalog(){
        return this.dir;
    }

    //Testowanie

    public void printBitVec(){
        Drive.printBitVec();
    }

    public void printDrive() {
        Drive.print();
    }
}