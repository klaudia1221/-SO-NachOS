package com.BamoOS.Modules.FileSystem;

public class File {

    public String FILE_NAME;	//Nazwa pliku
    public int FILE_SIZE;		//Rozmiar pliku
    int FIRST_BLOCK;			//Pierwszy blok zajmowany przez plik
    int LAST_BLOCK;
    String opened_file;
    boolean opened=false;

    public void setName(String fileName) { this.FILE_NAME = fileName; }

    public void setSize(int size) { this.FILE_SIZE = size; }

    public void setLast(int block) { this.LAST_BLOCK = block; }

    public void open(String fileContent) { this.opened_file=fileContent; opened=true; }

    public void close() { opened=false; this.opened_file = new String(); }

    //KONSTRUKTOR

    /*public File(String name, int size, int first){
        this.FILE_NAME=name;
        this.FILE_SIZE=size;
        this.FIRST_BLOCK=first;
        this.LAST_BLOCK=33;
    }*/

    public File(String name, int first){
        this.FILE_NAME=name;
        this.FILE_SIZE=0;
        this.FIRST_BLOCK=first;
        this.LAST_BLOCK=first;
    }

}
