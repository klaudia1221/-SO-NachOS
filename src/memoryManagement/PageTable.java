package memoryManagement;


public class PageTable {


    public int framesNumber[]; // w jakich ramkach jest dana strona programu
    public boolean validBit[];  //czy strona jest w RAM - true jesli jest

    public int processID;   // id procesu, do którego należy tablica stron

    // konstruktor tworzacy dla procesu tablice stron
    public PageTable(int procID, int processDataSize) {
        int count = Calc.howManyPages(processDataSize, 16);
        // ustawianie odpowiednich wartosci
        this.framesNumber = new int[count];
        this.validBit = new boolean[count];
        this.processID = procID;
        // wypelnienie
        for (int i = 0; i < count; i++) {
            this.framesNumber[i] = -1;
            this.validBit[i] = false;
            // System.out.println(framesNumber[i] +" "+ validBit[i]);
        }

    }

    // zwraca pozycje jaka w ramie ma dana stronica, zwraca -1 gdy stronicy nie
    // ma w ramie
    public int getPositionInRam(int pageNumber) {
        if (this.validBit[pageNumber] == false)
            return -1;
        else
            return this.framesNumber[pageNumber];
    }

    // zwraca nr stronicy ktora jest w danej ramce
    public int getIndex(int frame) {
        for (int i = 0; i < this.framesNumber.length; i++) {
            if (this.framesNumber[i] == frame)
                return i;
        }
        return -1;
    }

    // wypisuje tablice stronic
    public void writePageTable() {
        System.out.println("--------------------------------------------------------");
        System.out.println("TABLICA STRON dla procesu: " + this.processID);
        for (int i = 0; i < this.framesNumber.length; i++) {
            System.out.println("Indeks strony: " + i + " numer ramki: " + this.framesNumber[i] + " bit ważności: " + this.validBit[i]);
        }
        System.out.println("--------------------------------------------------------");

    }


}
