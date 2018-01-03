package com.BamoOS.Modules.MemoryManagment;


import javafx.util.Pair;

import java.util.*;

public class RAM {
    private char ram[] = new char[128];
    private boolean freeFrame[] = new boolean[8];
    private int processIDInFrame[] = new int[8];
    private Queue<Pair<Integer, Integer>> FIFO = new LinkedList<Pair<Integer, Integer>>();
    public Map<Integer, PageTable> pageTables = new HashMap<>();
    public ExchangeFile exchangeFile = new ExchangeFile();

    public char getFromRam(int index) {
        try {
            return ram[index];
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Niepoprawny indeks");
            return '$';
        }

    }

    // konstruktor, ustawienie podstawowych parametrow
    public RAM() {
        for (int i = 0; i < 8; i++) {
            this.freeFrame[i] = true;
            this.processIDInFrame[i] = (-1);
            for (int j = 0; j < 8; j++)
                this.ram[i * j] = ' ';
        }
    }

    // metoda dla interpretera, zwraca znak o ktory prosi interpreter
    public char getCommand(int programCounter, int processID, PageTable pt) {
        try {
            System.out.println("licznik rozkazow: " + programCounter + " id procesu: " + processID);
            int pageInd = Calc.whichPage(programCounter); //ktora to strona w pamieci logicznej
            System.out.println("indeks strony: " + pageInd);
            //najpierw sprawdzamy czy żądana strona znajduje sie w Ramie
            if (pt.validBit[pageInd]) {

                //  pt.framesNumber[pageInd]); // nr ramki w RAM, ktora zawiera nasz znak
                return getFromRam(pt.framesNumber[pageInd] * 16 + Calc.calcIndex(programCounter));

            } else {
                System.out.println("Blad strony");
                char[] list = new char[16];
                //musimy odszukac w ktorym miejscu w pliku wymiany jest pozadana przez nas strona
                for (int i = 0; i < 16; i++) {
                    list[i] = exchangeFile.readFromExchangeFile(processID, pageInd, i);
                    //   System.out.println(list[i]);
                }
                //teraz znajdujemy wolna ramke w RAM
                if (indexOfFreeFrame() == -1) {
                    System.out.println("Stan kolejki przed usunieciem strony: ");
                    this.writeQueue();
                    //nie ma miejsca w ramie wiec musimy jedna strone wyrzucic
                    //znajdujemy strone, zapisujemy do pliku wymiany i usuwamy

                    exchangeFile.saveToExchangeFile(FIFO.peek().getValue(), list, pageInd); //zapis strony do pliku wymiany
                    System.out.println("Zapisujemy wyrzucana strone do pliku wymiany");
                    System.out.println("Strona: " + FIFO.peek().getKey() + " nazwa procesu " + FIFO.peek().getValue());
                    int indexOfReleasedFrame = searchedPT(FIFO.peek().getValue()).framesNumber[FIFO.peek().getKey()];
                    System.out.println("Wyrzucono z kolejki fifo strone: " + FIFO.peek().getKey() + " dla procesu o ID: " + FIFO.peek().getValue() + " znajdujaca sie w ramce: " + indexOfReleasedFrame);
                    PageTable temp = searchedPT(FIFO.peek().getValue());

                    this.freeFrame[indexOfReleasedFrame] = true;  //ustawiamy zwlaniana ramke w Ramie na wolna
                    temp.framesNumber[indexOfReleasedFrame] = -1;   //modyfikujemy odpowiednia tablice stron
                    temp.validBit[indexOfReleasedFrame] = false;
                    pageTables.replace(FIFO.peek().getValue(), temp); //wrzucamy zmodyfikowana tablice stron
                    FIFO.poll();        //dopiero teraz usuwam z kolejki wybrana strone
                    System.out.println("Stan kolejki po usunieciu strony: ");
                    this.writeQueue();
                }
                //jest wolna ramka, wiec dzialamy
                int ind = indexOfFreeFrame();
                writeToRam(ind, list);
                processIDInFrame[ind] = processID;
                FIFO.add(new Pair<Integer, Integer>(pageInd, processID));
                pt.framesNumber[pageInd] = ind; //modyfikujemy tablice stron
                pt.validBit[pageInd] = true;
                this.freeFrame[ind] = false;
                System.out.println("Do ramki: " + ind + " wstawiono strone: " + pageInd + " procesu " + processID);

                return (getFromRam(ind * 16 + Calc.calcIndex(programCounter)));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Podano niepoprawna wartosc licznika rozkazow dla danego procesu- wyjscie poza zakres");
            return '&';
        }

    }

    public int indexOfFreeFrame() {
        for (int i = 0; i < 8; i++) {
            if (freeFrame[i]) return i;
        }
        System.out.println("Brak wolnych ramek w pamięci RAM");
        return -1;

    }

    public PageTable searchedPT(int processID) {
        return pageTables.get(processID);
    }

    //  usuwanie danego procesu z pamieci
    public void deleteProcessData(int processID) {
        System.out.println("Usuwanie procesu " + processID + " z pamieci");
        // nie usuwamy nic w ramie, maja byc smieci
        for (int i = 0; i < 8; i++) {
            if (this.processIDInFrame[i] == processID) {
                this.freeFrame[i] = true;
            }
        }
        Iterator<Pair<Integer, Integer>> iterator = FIFO.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().getValue().equals(processID))
                iterator.remove();
        }
        int nrOfPages = pageTables.get(processID).framesNumber.length;
        this.pageTables.remove(processID); //usuwamy tablice stron dla danego procesu
        this.exchangeFile.deleteDataFromExchangeFile(processID, nrOfPages); //usuwamy dane z pliku wymiany
    }


//    // wypisanie procesow bedacych w pamieci
//    public void writeProcessesNamesInRam() {
//        for (int i = 0; i < this.pageTables.size(); i++)
//            System.out.println(pageTables.get(i).processID);
//    }

    // wypisanie zawartosci ramu
    public void writeRAM() {
        for (int i = 0; i < 8; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 16; j++)
                System.out.print(ram[16 * i + j]);
            System.out.println();
        }
    }

    private void writeToRam(int index, char[] content) {
        for (int i = 0; i < 16; i++) {
            ram[index * 16 + i] = content[i];
        }
    }


    // wypisanie aktualnego stanu kolejki fifo
    public void writeQueue() {
        System.out.println("Kolejka FIFO: " + this.FIFO);
    }
}

