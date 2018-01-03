package com.BamoOS.Modules.FileSystem;

import com.BamoOS.Modules.ACL.User;
import com.BamoOS.Modules.ConditionVariable.ConditionVariable;
import com.BamoOS.Modules.ConditionVariable.IConditionVariable;
import com.BamoOS.Modules.ProcessManager.IProcessManager;
import com.BamoOS.Modules.ProcessManager.ProcessManager;

public class File extends FileBase{

    public String FILE_NAME;	//Nazwa pliku
    public int FILE_SIZE;		//Rozmiar pliku
    int FIRST_BLOCK;			//Pierwszy blok zajmowany przez plik
    int LAST_BLOCK;
    String opened_file;
    boolean opened=false;
    public IConditionVariable cv;

    public void setName(String fileName) { this.FILE_NAME = fileName; }

    public void setSize(int size) { this.FILE_SIZE = size; }

    public void setLast(int block) { this.LAST_BLOCK = block; }

    public void open(String fileContent) {
        this.opened_file+=fileContent; opened=true;
    }

    public void close() { opened=false; this.opened_file = new String(); }

    //KONSTRUKTOR

    /*public File(String name, int size, int first){
        this.FILE_NAME=name;
        this.FILE_SIZE=size;
        this.FIRST_BLOCK=first;
        this.LAST_BLOCK=33;
    }*/

    public File(String name, int first, User user, IProcessManager processManager){
        super(user);
        this.FILE_NAME=name;
        this.FILE_SIZE=0;
        this.FIRST_BLOCK=first;
        this.LAST_BLOCK=first;
        this.cv = new ConditionVariable(processManager);
        //Pytanie do michała, dlaczego to jest kalasa String,
        // a nie sam string
        this.opened_file=new String();
    }

    //pomocnicze

    public String getCon(){
        return this.opened_file;
    }
}
