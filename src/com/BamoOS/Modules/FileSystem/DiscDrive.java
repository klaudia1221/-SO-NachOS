public class DiscDrive {
    final int DISC_SIZE = 1024;                     //Rozmiar dysku
    final int BLOCK_SIZE = 32;                      //Rozmiar bloku dyskowego
    final int BLOCKS_AMOUNT = DISC_SIZE/BLOCK_SIZE;                   //Ilosc blokow dyskowych
    int FREE_BLOCKS = 32;                           //Ilosc wolnych blokow dyskowych

    private char[] disc = new char[DISC_SIZE];              //Tablica znakow reprezetujaca dysk

    boolean[] bitVec = new boolean[BLOCKS_AMOUNT];  //Wektor bitowy

    //Metody pomocnicze

    void putByte(char b, int position){
        disc[position] = b;
    }

    int lastByte(int block_num) {
        return (int) disc[(block_num+1)*32-1];
    }

    char getAt(int index) { return this.disc[index]; }

    //KONSTRUKTOR

    public DiscDrive(){for (int i=0; i<BLOCKS_AMOUNT; i++){bitVec[i]=true;}}

    //TESTOWANIE DYSKU

    public void fill(){
        for (int i=0; i<DISC_SIZE; i++){
            int pom = i%32;
            disc[i]=(char) (65+pom);
        }
    }

    public void fill1(char a){
        for (int i=0; i<DISC_SIZE; i++){
            disc[i]=a;
        }
    }

    public void fill2(){
        for (int i=0; i<32; i++){
            disc[(i+1)*32-1] = '1';
        }
    }

    public void print(){
        for (int i=0; i<DISC_SIZE; i++){
            if (i%32==0 && i>0){
                System.out.println();
            }
            System.out.print('[');
            System.out.print(disc[i]);
            System.out.print(']');

        }
    }

    public void printBitVec(){
        for (boolean a : bitVec) {
            if (a==true){
                System.out.print(1);
            }
            else {
                System.out.print(0);
            }
        }
    }
}
