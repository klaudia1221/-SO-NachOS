package com.BamoOS.Modules.MemoryManagment;

public class Calc {


    public static int howManyPages(int x, int pageSize) {
        if (x % 16 == 0) {
            return x / 16;
        } else return (x / 16) + 1;
    }

    public static int whichPage(int lr) {
        if ((lr + 1) % 16 == 0) return ((lr + 1) / 16) - 1;
        else return ((lr + 1) / 16);
    }

    public static int calcIndex(int lr) {
        return lr % 16;
    }
}
