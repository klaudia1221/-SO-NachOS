package com.BamoOS.Modules.memoryManagement;

public class Main {
    public static void main(String args[]) {
        RAM ram = new RAM();
        char[] testTable1 = new char[]{'s', 'g', '1', '2', '3', '4', '5', '6', '7', '8', '9', 't', 'a', 'b', 'l', 'i', 'c', 'a', 't', 'e', 's', 't', 'o', 'w', 'a', 't', 'a', 'b', 'l', 'i', 'c', 'a', 't', 'e', 's', 't', 'o', 'w', 'a', 't', 'a', 'b', 'l', 'i', 'c', 'a', 't', 'e', 's', 't', 'o', 'w', 'a'};
        char[] zmiana = new char[]{'z', 'm', 'i', 'a', 'n', 'a', 'z', 'm', 'i', 'a', 'n', 'a', 'z', 'm', 'i', 'a'};
        char[] testTable2 = new char[]{'d', 'r', 'u', 'g', 'a', 't', 'a', 'b', 'l', 'i', 'c', 'a', 't', 'e', 's', 't', 'o', 'w', 'a'};
        char[] testTable3 = new char[]{'t', 'r', 'z', 'e', 'c', 'i', 'a', 't', 'a', 'b', 'l', 'i', 'c', 'a', 't', 'e', 's', 't', 'o', 'w', 'a'};
        char[] testTable4 = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9'};
        char[] testTable5 = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', 't', 'a', 'b', 'l', 'i', 'c', 'a', 't', 'e', 's', 't', 'o', 'w', 'a'};

        PageTable pt1 = new PageTable(1, testTable1.length);
        PageTable pt2 = new PageTable(2, testTable2.length);
        PageTable pt3 = new PageTable(3, testTable3.length);
        PageTable pt4 = new PageTable(4, testTable4.length);
        PageTable pt5 = new PageTable(5, testTable5.length);
        ram.pageTables.put(pt1.processID, pt1);
        ram.pageTables.put(pt2.processID, pt2);
        ram.pageTables.put(pt3.processID, pt3);
        ram.pageTables.put(pt4.processID, pt4);
        ram.pageTables.put(pt5.processID, pt5);


        ram.exchangeFile.writeToExchangeFile(1, testTable1);
        ram.exchangeFile.writeToExchangeFile(2, testTable2);
        ram.exchangeFile.writeToExchangeFile(3, testTable3);
        ram.exchangeFile.writeToExchangeFile(4, testTable4);
        ram.exchangeFile.writeToExchangeFile(5, testTable5);

        System.out.println(ram.exchangeFile.readFromExchangeFile(1, 0, 15));
        ram.exchangeFile.saveToExchangeFile(3, zmiana, 0);
        ram.exchangeFile.showContent(); //pokaz zawartosc pliku wymiany
        char w = ram.getCommand(14, 3, pt3);
        w = ram.getCommand(8, 1, pt1);
        w = ram.getCommand(24, 2, pt2);
        ram.exchangeFile.saveToExchangeFile(2, zmiana, 1); //podajesz nazwe procesu i indeks strony w ktorej chcesz
        //dokonac zmian, drugi argument to koniecznie 16-elementowa tablica char

        w = ram.getCommand(29, 3, pt3);
        w = ram.getCommand(2, 3, pt3);
        w = ram.getCommand(34, 1, pt1);
        w = ram.getCommand(4, 2, pt2);
        w = ram.getCommand(0, 2, pt2);
        w = ram.getCommand(8, 1, pt1);
        w = ram.getCommand(24, 1, pt1);
        w = ram.getCommand(34, 1, pt1);
        w = ram.getCommand(7, 4, pt4);

        ram.writeRAM();

        w = ram.getCommand(24, 1, pt1);
        w = ram.getCommand(14, 5, pt5);
        ram.writeQueue();
        ram.writeRAM();


        w = ram.getCommand(24, 1, pt1);

        ram.writeQueue();

        ram.deleteProcessData(1);

        ram.exchangeFile.showContent();
        ram.pageTables.get(2).writePageTable(); //lub pt2.writePageTable();
        ram.pageTables.get(3).writePageTable();
        ram.pageTables.get(4).writePageTable();
        ram.pageTables.get(5).writePageTable();

        ram.writeRAM();

        ram.writeQueue();


    }
}
