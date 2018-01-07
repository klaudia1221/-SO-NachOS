package com.NachOS.Modules.MemoryManagment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExchangeFile {
    private List<List<Character>> exchangeFile = new ArrayList<List<Character>>(); //tablica dwuwymiarowa, kazdy rzad zawiera 1 strone
    private HashMap<Integer, Integer> map; //K=nazwa procesu, V-index 1 strony
    int startIndex;
    int pageSize;

    public ExchangeFile() {
        startIndex = 0;
        pageSize = 16;
        map = new HashMap<>();
    }

    public void writeToExchangeFile(int processID, char[] data, int additionalSpace) {

        int howManyPages = Calc.howManyPages(data.length+additionalSpace, pageSize);
        map.put(processID, startIndex);
        System.out.println("do mapy wlozono: " + processID + " z ind " + startIndex);
        List<Character> cList = new ArrayList<Character>();
        for (char c : data) {
            cList.add(c);
        }
        for (int i = 0; i < additionalSpace; i++) { //dodatkowa pamiec, ktorej chce user
            cList.add('^');
        }
        while (cList.size() % 16 != 0) { //dopelniamy strone do 16
            cList.add('#');
        }
        ArrayList<Character> temp;
        for (int i = 0; i < howManyPages; i++) {
            temp = new ArrayList<>();
            for (int j = 0; j < pageSize; j++) {
                //   System.out.println(cList.get(i * pageSize + j));

                temp.add(cList.get(i * pageSize + j));
            }
            exchangeFile.add(temp);
            startIndex++;
        }
    }

    public char readFromExchangeFile(int processID, int pageIndex, int indexInPage) {
        try {
            int a = map.get(processID) + pageIndex;
            return exchangeFile.get(a).get(indexInPage);

        } catch (IndexOutOfBoundsException e) {
            System.out.println("Podano niepoprawny indeks, operacja zakonczona niepowodzeniem ");
            return '#';
        }
    }

    public void saveToExchangeFile(int processID, char[] data, int pageIndex) {
        try {
            System.out.println("Zapis strony do pliku wymiany");
            if (data.length == pageSize) {
                int a = map.get(processID);
                for (int i = 0; i < data.length; i++) {

                    exchangeFile.get(a + pageIndex).set(i, data[i]);
                }
            } else System.out.println("Niepoprawny rozmiar danych, opracja zapisu danych do pliku wymiany nieudana ");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Nieudana próba zapisu strony do pliku wymiany, zły indeks strony");
        }
    }


    public void showContent() {
        System.out.println("Zawartosc pliku wymiany: ");
        System.out.println(this.exchangeFile.toString());


    }

    public void deleteDataFromExchangeFile(int processID, int howManyPages) {
        System.out.println("Usuwanie z pliku wymiany: " + howManyPages + " stron procesu: " + processID);
        List<Character> tab = new ArrayList<Character>();
        for (int j = 0; j < 16; j++) {
            tab.add(' ');
        }
        for (int i = 0; i < howManyPages; i++) {

            exchangeFile.set(map.get(processID) + i, tab);

        }
    }
}
