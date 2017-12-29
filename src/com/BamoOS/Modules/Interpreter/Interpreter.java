import java.lang.String;
import java.util.*;

public class Interpreter {

    /**
     Rozkazy:
     •
     •AX reg num – dodaje liczbę do rejestru, 
     reg [address] - dodaje zawartość adresu do rejestru,
     •SB reg1 reg2 - odejmuje od rejestru1 zawartość rejestru2, 
     •SX reg num – odejmuje liczbę od rejestru, 
        reg [address] - odejmuje zawartość adresu od rejestru,
     •DC reg - zwiększa zawartość rejestru o 1,
     •IC reg - zmniejsza zawartość rejestru o 1,
     •MU reg1 reg2 – mnoży rejestr 1 przez rejestr 2, 
     •MX reg num – mnoży rejestr przez liczbę, 
        reg [address] - mnoży zawartość adresu razy zawartość rejestru,
     •DV reg1 reg2 - dzieli zawartość rejestru1 przez zawartość rejestru2, 
     •DX reg num – dzieli rejestr od rejestru, 
        reg [address] - dzieli zawartość rejestru przez liczbę z danej komórki,
     •MD reg1 reg2 reg3 - reszta z dzielenia rejestru1 przez rejestr2 zapisywana 2 rejestrze3,
     •XM reg1 num reg2  - reszta z dzielenia rejestru1 przez liczbę zapisywana w rejestrze2,
        reg1 [address] reg2 - reszta z dzielenia rejestru1 przez zawartość danej komórki zapisywana w rejestrze2,
     •MV reg1 reg2 – kopiuje wartość rejestru 2 do rejestru 1,
      •MZ address reg - zapisuje do pamięci zawartość rejestru pod wskazanym adresem,
     •MO reg n – umieszcza w rejestrze wartość n, 
     •MY reg address - umieszcza w rejestrze zawartość pamiętaną pod wskazanym adresem,
      •CE file_name - tworzy pusty plik o podanej nazwie, 
      •CF file_name file_content - tworzy plik z zawartością,
     •AF file_name file_content - dodaje dane na końcu pliku,
     •DF file_name - usuwa plik o danej nazwie, 
     •RF file_name - czyta plik o podanej nazwie,
      •RN old_file_name new_file_name - zmienia nazwę pliku
     •NP process_name - tworzy procesu o danej nazwie, 
     •NG process_name - tworzy nową grupę oraz pierwszy proces o danej nazwie, 
     •KP PID - usunięcie procesu o danym PID, 
     •RP PID  – uruchamia proces o danej PID, 
     •KG PGID - usunięcie grupy procesów o danym PGID,
     •SS PID state - zmiana stanu procesu na dany,
     •RM sender - czytanie komunikat, 
     •SM receiver message - wysłanie komunikatu, 
     •LM PID - czytanie komunikatów od danego procesu,
     •JP counter - skacze do innego rozkazu poprzez zmianę licznika, 
     •JZ reg n - skok przy zerowej zawartości rejestru będącego argumentem, 
     •EX - kończy program,
     **/

    private int A = 0;
    private int B = 0;
    private int C = 0;
    private int PC = 0;
    private int PID = 0;

    private ProcesorInterface procesor;
    private IProcessManager processManager;
    private RAM memory;
    private FileSystemInterface fileSystem;
    private IPCB PCB;
    private BoxOffice boxOffice;

    Interpreter(ProcesorInterface procesor, RAM memory, IProcessManager processManager, FileSystemInterface fileSystem, IPCB PCB, BoxOffice boxOffice) {
        this.procesor = procesor;
        this.memory = memory;
        this.processManager = processManager;
        this.fileSystem = fileSystem;
        this.PCB = PCB;
        this.boxOffice = boxOffice;
    }
    
    public void set_A(){
        this.A = PCB.GetRegister(IPCB.Register.A);
    }

    public void set_B(){
        this.B = PCB.GetRegister(IPCB.Register.B);
    }

    public void set_C(){
        this.C = PCB.GetRegister(IPCB.Register.C);
    }
    
    public void set_PC(){
        this.PC = PCB.GetCounter();
    }
    
    public void set_PID(){
        this.PID = PCB.GetPID();
    }

    public int get_A(){
        return A;
    }

    public int get_B(){
        return B;
    }

    public int get_C(){
        return C;
    }

    public int get_PC(){
        return PC;
    }
    
    private String DownloadOrder() {
        String order="";
        int address = PC;
        while(memory.readMemory(address)!=';') {
            order += memory.readMemory(address);
            address++;
        }
        return order;
    }
    
    private void DownloadRegisters(){
        set_A();
        set_B();
        set_C();
        set_PC();
        set_PID();
    }

    public void RegisterStatus() {
        System.out.println("PID: " + get_PID());
        System.out.println("Register A: " + get_A());
        System.out.println("Register B: " + get_B());
        System.out.println("Register C: " + get_C());
        System.out.println("Register PC: " + get_PC());
        System.out.println();
    }

    private void SaveRegister() {
        PCB.SetRegister(IPCB.Register.A, get_A());
        PCB.SetRegister(IPCB.Register.B, get_B());
        PCB.SetRegister(IPCB.Register.C, get_C());
        PCB.SetCounter(get_PC());
    }

    private void AD(String [] order){
        String reg_1 = order[1];
        String reg_2 = order[2];

        if (reg_1 == "A") {
            if (reg_2 == "B") {
                A += B;
            } else if (reg_2 == "C") {
                A += C;
            } else {
                System.out.println("Incorrect register.");
            }
        } else if (reg_1 == "B") {
            if (reg_2 == "A") {
                B += A;
            } else if (reg_2 == "C") {
                B += C;
            } else {
                System.out.println("Incorrect register.");
            }
        } else if (reg_1 == "C") {
            if (reg_2 == "A") {
                C += A;
            } else if (reg_2 == "B") {
                C += B;
            } else {
                System.out.println("Incorrect register.");
            }
        } else {
            System.out.println("Incorrect register.");
        }
    }

    private void AX(String[] order){
        String reg = order[1];
        int len = order[2].length();

        if ((order[2].substring(0,1) == "[")&&(order[2].substring(len-2,len-1))=="]"){
            String raw_address = order[2];
            raw_address = raw_address.replaceAll("[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);

            char pom = memory.readMemory(address);
            int address_content = (int) memory.readMemory(address);

            if (pom != '#' && Character.isDigit(pom)) {

                if (reg == "A") {
                    A += address_content;
                } else if (reg == "B") {
                    B += address_content;
                } else if (reg == "C") {
                    C += address_content;
                } else {
                    System.out.println("Incorrect register.");
                }
            }else {
                System.out.println("Address is empty.");
            }
        }else if ((order[2].substring(0,1) == "[")&&(order[2].substring(len-2,len-1))!="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) != "[")&&(order[2].substring(len-2,len-1))=="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) == "[")&&(order[2].substring(1,2))=="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) != "[")&&(order[2].substring(len-2,len-1))!="]"){
            int val = Integer.parseInt(order[2]);

            if (reg == "A") {
                A += val;
            } else if (reg == "B") {
                B += val;
            } else if (reg == "C") {
                C += val;
            } else {
                System.out.println("Incorrect register.");
            }
        }
    }

    private void SB(String[] order){
        String reg_1 = order[1];
        String reg_2 = order[2];

        if (reg_1 == "A") {
            if (reg_2 == "B") {
                A -= B;
            } else if (reg_2 == "C") {
                A -= C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1 == "B") {
            if (reg_2 == "A") {
                B -= A;
            } else if (reg_2 == "C") {
                B -= C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1 == "C") {
            if (reg_2 == "A") {
                C -= A;
            } else if (reg_2 == "B") {
                C -= B;
            } else System.out.println("Incorrect register.");
        } else {
            System.out.println("Incorrect register.");
        }
    }

    private void SX(String[] order){
        String reg = order[1];
        int len = order[2].length();

        if ((order[2].substring(0,1) == "[")&&(order[2].substring(len-2,len-1))=="]"){
            String raw_address = order[2];
            raw_address = raw_address.replaceAll("[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);

            char pom = memory.readMemory(address);
            int address_content = (int) memory.readMemory(address);

            if (pom != '#' && Character.isDigit(pom)) {

                if (reg == "A") {
                    A -= address_content;
                } else if (reg == "B") {
                    B -= address_content;
                } else if (reg == "C") {
                    C -= address_content;
                } else {
                    System.out.println("Incorrect register.");
                }
            }else {
                System.out.println("Address is empty.");
            }
        }else if ((order[2].substring(0,1) == "[")&&(order[2].substring(len-2,len-1))!="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) != "[")&&(order[2].substring(len-2,len-1))=="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) == "[")&&(order[2].substring(1,2))=="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) != "[")&&(order[2].substring(len-2,len-1))!="]"){
            int val = Integer.parseInt(order[2]);

            if (reg == "A") {
                A -= val;
            } else if (reg == "B") {
                B -= val;
            } else if (reg == "C") {
                C -= val;
            } else {
                System.out.println("Incorrect register.");
            }
        }
    }

    private void DC(String[] order){
        String reg = order[1];

        if (reg == "A") {
            A -= 1;
        } else if (reg == "B") {
            B -= 1;
        } else if (reg == "C") {
            C -= 1;
        } else {
            System.out.println("Incorrect register.");
        }
    }

    private void IC(String[] order){
        String reg = order[1];
        if (reg == "A") {
            A += 1;
        } else if (reg == "B") {
            B += 1;
        } else if (reg == "C") {
            C += 1;
        } else {
            System.out.println("Incorrect register.");
        }
    }

    private void MU(String[] order){
        String reg_1 = order[1];
        String reg_2 = order[2];

        if (reg_1 == "A") {
            if (reg_2 == "B") {
                A *= B;
            } else if (reg_2 == "C") {
                A *= C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1 == "B") {
            if (reg_2 == "A") {
                B *= A;
            } else if (reg_2 == "C") {
                B *= C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1 == "C") {
            if (reg_2 == "A") {
                C *= A;
            } else if (reg_2 == "B") {
                C *= B;
            } else {
                System.out.println("Incorrect register.");
            }
        } else {
            System.out.println("Incorrect register.");
        }
    }

    private void MX(String[] order) {
        String reg = order[1];
        int len = order[2].length();

        if ((order[2].substring(0,1) == "[")&&(order[2].substring(len-2,len-1))=="]"){
            String raw_address = order[2];
            raw_address = raw_address.replaceAll("[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);

            char pom = memory.readMemory(address);
            int address_content = (int) memory.readMemory(address);

            if (pom != '#' && Character.isDigit(pom)) {

                if (reg == "A") {
                    A *= address_content;
                } else if (reg == "B") {
                    B *= address_content;
                } else if (reg == "C") {
                    C *= address_content;
                } else {
                    System.out.println("Incorrect register.");
                }
            }else {
                System.out.println("Address is empty.");
            }
        }else if ((order[2].substring(0,1) == "[")&&(order[2].substring(len-2,len-1))!="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) != "[")&&(order[2].substring(len-2,len-1))=="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) == "[")&&(order[2].substring(1,2))=="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) != "[")&&(order[2].substring(len-2,len-1))!="]"){
            int val = Integer.parseInt(order[2]);

            if (reg == "A") {
                A *= val;
            } else if (reg == "B") {
                B *= val;
            } else if (reg == "C") {
                C *= val;
            } else {
                System.out.println("Incorrect register.");
            }
        }
    }

    private void DV(String[] order) {
        String reg_1 = order[1];
        String reg_2 = order[2];

        if (reg_1 == "A") {
            if (reg_2 == "B"&& B!=0) {
                A /= B;
            } else if (reg_2 == "C"&& C!=0) {
                A /= C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1 == "B") {
            if (reg_2 == "A"&& A!=0) {
                B /= A;
            } else if (reg_2 == "C"&& C!=0) {
                B /= C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1 == "C") {
            if (reg_2 == "A" && A!=0) {
                C /= A;
            } else if (reg_2 == "B" && B!=0) {
                C /= B;
            } else System.out.println("Incorrect register.");
        } else {
            System.out.println("Incorrect register.");
        }
    }

    private void DX(String[] order) {
        String reg = order[1];
        int len = order[2].length();

        if ((order[2].substring(0,1) == "[")&&(order[2].substring(len-2,len-1))=="]"){
            String raw_address = order[2];
            raw_address = raw_address.replaceAll("[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);

            char pom = memory.readMemory(address);
            int addres_content = (int) memory.readMemory(address);

            if (pom != '#'&& addres_content!=0 && Character.isDigit(pom)) {
                if (reg == "A") {
                    A /= addres_content;
                } else if (reg == "B") {
                    B /= addres_content;
                } else if (reg == "C") {
                    C /= addres_content;
                } else {
                    System.out.println("Incorrect register.");
                }
            }else if (pom == '#'){
                System.out.println("Address is empty.");
            }else if(addres_content == 0) {
                System.out.println("Not divide by zero.");
            }
        }else if ((order[2].substring(0,1) == "[")&&(order[2].substring(len-2,len-1))!="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) != "[")&&(order[2].substring(len-2,len-1))=="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) == "[")&&(order[2].substring(1,2))=="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) != "[")&&(order[2].substring(len-2,len-1))!="]"){
            int val = Integer.parseInt(order[2]);

            if (val != 0) {
                if (reg == "A") {
                    A /= val;
                } else if (reg == "B") {
                    B /= val;
                } else if (reg == "C") {
                    C /= val;
                } else {
                    System.out.println("Incorrect register.");
                }
            }
        }
    }

    private void MD(String[] order) {
        String reg_1 = order[1];
        String reg_2 = order[2];
        String reg_3 = order[3];

        if (reg_1 == "A") {
            if (reg_2 == "B" && reg_3 == "A" && B != 0) {
                A = A%B;
            } else if (reg_2 == "B" && reg_3 == "B" && B != 0) {
                B = A%B;
            } else if (reg_2 == "B" && reg_3 == "C" && B != 0) {
                C = A%B;
            } else if (reg_2 == "C" && reg_3 == "A" && C != 0){
                A = A%C;
            } else if (reg_2 == "C" && reg_3 == "B" && C != 0){
                B = A%C;
            } else if (reg_2 == "C" && reg_3 == "C" && C != 0){
                C = A%C;
            } else {
                System.out.println("Incorrect register.");
            }
        } else if (reg_1 == "B") {
            if (reg_2 == "A" && reg_3 == "A" && A != 0) {
                A = B%A;
            } else if (reg_2 == "A" && reg_3 == "B" && A != 0) {
                B = B%A;
            } else if (reg_2 == "A" && reg_3 == "C" && A != 0) {
                C = B%A;
            } else if (reg_2 == "C" && reg_3 == "A" && C != 0){
                A = B%C;
            } else if (reg_2 == "C" && reg_3 == "B" && C != 0){
                B = B%C;
            } else if (reg_2 == "C" && reg_3 == "C" && C != 0){
                C = B%C;
            } else {
                System.out.println("Incorrect register.");
            }
        } else if (reg_1 == "C") {
            if (reg_2 == "A" && reg_3 == "A" && A != 0) {
                A = C%A;
            } else if (reg_2 == "A" && reg_3 == "B" && A != 0) {
                B = C%A;
            } else if (reg_2 == "A" && reg_3 == "C" && A != 0) {
                C = C%A;
            } else if (reg_2 == "C" && reg_3 == "A" && B != 0){
                A = C%B;
            } else if (reg_2 == "C" && reg_3 == "B" && B != 0){
                B = C%B;
            } else if (reg_2 == "C" && reg_3 == "C" && B != 0){
                C = C%B;
            } else {
                System.out.println("Incorrect register.");
            }
        } else {
            System.out.println("Incorrect register.");
        }
    }

    private void XM(String[] order) {
        String reg_1 = order[1];
        String reg_3 = order[3];

        int len = order[2].length();

        if ((order[2].substring(0,1) == "[")&&(order[2].substring(len-2,len-1))=="]"){
            String raw_address = order[2];
            raw_address = raw_address.replaceAll("[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);

            char pom = memory.readMemory(address);
            int address_content = (int) memory.readMemory(address);

            if (pom != '#' && Character.isDigit(pom)) {
                if (reg_1 == "A") {
                    if (reg_3 == "A") {
                        A = A % address_content; }
                    if (reg_3 == "B") {
                        B = A % address_content; }
                    if (reg_3 == "C") {
                        C = A % address_content; }
                } else if (reg_1 == "B") {
                    if (reg_3 == "A") {
                        A = B % address_content; }
                    if (reg_3 == "B") {
                        B = B % address_content; }
                    if (reg_3 == "C") {
                        C = B % address_content; }
                } else if (reg_1 == "C") {
                    if (reg_3 == "A") {
                        A = C % address_content; }
                    if (reg_3 == "B") {
                        B = C % address_content; }
                    if (reg_3 == "C") {
                        C = C % address_content; }
                } else {
                    System.out.println("Incorrect register.");
                }
            }else {
                System.out.println("Address is empty.");
            }
        }else if ((order[2].substring(0,1) == "[")&&(order[2].substring(len-2,len-1))!="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) != "[")&&(order[2].substring(len-2,len-1))=="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) == "[")&&(order[2].substring(1,2))=="]") {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1) != "[")&&(order[2].substring(len-2,len-1))!="]"){
            int val = Integer.parseInt(order[2]);

            if(val != 0) {
                if (reg_1 == "A") {
                    if (reg_3 == "A") {
                        A = A % val;
                    } else if (reg_3 == "B") {
                        B = A % val;
                    } else if (reg_3 == "C") {
                        C = A % val;
                    } else {
                        System.out.println("Incorrect register.");
                    }
                } else if (reg_1 == "B") {
                    if (reg_3 == "A") {
                        A = B % val;
                    } else if (reg_3 == "B") {
                        B = B % val;
                    } else if (reg_3 == "C") {
                        C = B % val;
                    } else {
                        System.out.println("Incorrect register.");
                    }
                } else if (reg_1 == "C") {
                    if (reg_3 == "A") {
                        A = C % val;
                    } else if (reg_3 == "B") {
                        B = C % val;
                    } else if (reg_3 == "C") {
                        C = C % val;
                    } else {
                        System.out.println("Incorrect register.");
                    }
                } else {
                    System.out.println("Incorrect register.");
                }
            }
        }

    }

    private void MV(String[] order) {
        String reg_1 = order[1];
        String reg_2 = order[2];

        if (reg_1 == "A") {
            if (reg_2 == "B") {
                A = B;
            } else if (reg_2 == "C") {
                A = C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1 == "B") {
            if (reg_2 == "A") {
                B = A;
            } else if (reg_2 == "C") {
                B = C;
            } else {
                System.out.println("Incorrect register.");
            }

        } else if (reg_1 == "C") {
            if (reg_2 == "A") {
                C = A;
            } else if (reg_2 == "B") {
                C = B;
            } else {
                System.out.println("Incorrect register.");
            }
        } else {
            System.out.println("Incorrect register.");
        }

    }

    private void MZ(String[] order) {
        String raw_address = order[1];
        String register = order[2];
        String[] split_address = raw_address.split("");

        if((split_address[0] != "[")|| (split_address[raw_address.length()-1] != "]")){
            System.out.println("Incorrect address.");
        }else {
            raw_address = raw_address.replaceAll("[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);
            if (register == "A") {
                memory.writeMemory((char) A, address);
            } else if(register == "B"){
                memory.writeMemory((char) B, address);
            } else if(register == "C"){
                memory.writeMemory((char) C, address);
            } else {
                System.out.println("Incorrect register.");
            }
        }
    }

    private void MO(String[] order) {
        String reg = order[1];
        int val = Integer.parseInt(order[2]);

        if (reg == "A") {
            A = val;
        } else if (reg == "B") {
            B = val;
        } else if (reg == "C") {
            C = val;
        } else {
            System.out.println("Incorrect register.");
        }
    }

    private void MY(String[] order) {
        String register = order[1];
        String raw_address = order[2];
        String[] split_address = raw_address.split("");

        if ((split_address[0] != "[") || (split_address[raw_address.length() - 1] != "]")) {
            System.out.println("Incorrect address.");
        } else {
            raw_address = raw_address.replaceAll("[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);
            char pom = memory.readMemory(address);
            if (pom != '#') {
                if (register == "A") {
                    A = memory.readMemory(address);
                } else if (register == "B") {
                    B = memory.readMemory(address);
                } else if (register == "C") {
                    C = memory.readMemory(address);
                } else {
                    System.out.println("Incorrect register.");
                }
            } else {
                System.out.println("Address is empty.");
            }
        }
    }

    private void CE(String[] order) {
        try {
            String filename = order[1];
            fileSystem.createEmptyFile(filename);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void CF(String[] order) {
        try {
            String filename = order[1];
            String fileContent = order[2];
            fileSystem.createFile(filename, fileContent);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void AF(String[] order) {
        try {
            String filename = order[1];
            String fileContent = order[2];
            fileSystem.appendFile(filename, fileContent);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void DF(String[] order) {
        try {
            String filename = order[1];
            fileSystem.deleteFile(filename);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void RF(String[] order) {
        try {
            String filename = order[1];
            String fileContent = fileSystem.readFile(filename);
            System.out.println("File content:");
            System.out.println(fileContent);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void RN(String[] order) {
        try {
            String oldName = order[1];
            String newName = order[2];
            fileSystem.renameFile(oldName, newName);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void NP(String[] order) {
        try {
            String ProcessName = order[1];
            processManager.NewProcess(ProcessName);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void NG(String[] order) {
        try {
            String ProcessName = order[1];
            processManager.NewProcessGroup(ProcessName);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void KP(String[] order) {
        try {
            int PID = Integer.parseInt(order[1]);
            processManager.KillProcess(PID);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    private void SS(String[] order) {
        try {
            int PID = Integer.parseInt(order[1]);
            String state = order[2];
            if (state == "ACTIVE") {
                PCB.SetState(PID, IPCB.State.ACTIVE);
            } else if (state == "WAITING") {
                PCB.SetState(PID, IPCB.State.WAITING);
            } else if (state == "READY") {
                PCB.SetState(PID, IPCB.State.READY);
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void RP(String[] order) {
        try {
            int PID = Integer.parseInt(order[1]);
            procesor.RunProcess(PID);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void KG(String[] order) {
        try {
            int PGID = Integer.parseInt(order[1]);
            processManager.KillProcessGroup(PGID);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void RM(String[] order) {
        try {
            int PID = Integer.parseInt(order[1]);
            String message = boxOffice.receiveMessage(PID);
            System.out.println(message);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void SM(String[] order) {
        try {
            int PID = Integer.parseInt(order[1]);
            String message = order[2];
            boxOffice.sendMessage(PID, message);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void LM(String[] order) {
        try {
            int PID = Integer.parseInt(order[1]);
            ArrayList<ArrayList<BoxOffice.Message>> message = boxOffice.printMessage(PID);
            System.out.println(message);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void JP(String[] order) {
        try {
            int counter = Integer.parseInt(order[1]);
            PC = counter;
        } catch (Exception e) {
            System.out.println();
        }
    }

    private void JZ(String[] order) {
        try {
            String register = order[1];
            int counter = Integer.parseInt(order[1]);

            if (register == "A") {
                if (A == 0) {
                    PC = counter;
                }
            } else if (register == "B") {
                if (B == 0) {
                    PC = counter;
                }
            } else if (register == "C") {
                if (C == 0) {
                    PC = counter;
                }
            } else {
                System.out.println("Incorrect register");
            }
        } catch (Exception e) {
            System.out.println();
        }
    }

    public void Exe() {
        DownloadRegisters();
        RegisterStatus();

        String raw_order = DownloadOrder();

        try {
            String[] order = raw_order.split(" ");
            String operation = order[0];

            if (operation == "AD") {
                AD(order);
            } else if (operation == "AX") {
                AX(order);
            } else if (operation == "SB") {
                SB(order);
            } else if (operation == "SX") {
                SX(order);
            } else if (operation == "DC") {
                DC(order);
            } else if (operation == "IC") {
                IC(order);
            } else if (operation == "MU") {
                MU(order);
            } else if (operation == "MX") {
                MX(order);
            } else if (operation == "DV") {
                DV(order);
            } else if (operation == "DX") {
                DX(order);
            } else if (operation == "MD") {
                MD(order);
            } else if (operation == "XM") {
                XM(order);
            } else if (operation == "MV") {
                MV(order);
            } else if (operation == "MZ") {
                MZ(order);
            } else if (operation == "MO") {
                MO(order);
            } else if (operation == "MY") {
                MY(order);
            } else if (operation == "CE") {
                CE(order);
            } else if (operation == "CF") {
                CF(order);
            } else if (operation == "AF") {
                AF(order);
            } else if (operation == "DF") {
                DF(order);
            } else if (operation == "RF") {
                RF(order);
            } else if (operation == "RN") {
                RN(order);
            } else if (operation == "NP") {
                NP(order);
            } else if (operation == "NG") {
                NG(order);
            } else if (operation == "KP") {
                KP(order);
            } else if (operation == "SS") {
                SS(order);
            } else if (operation == "RP") {
                RP(order);
            } else if (operation == "KG") {
                KG(order);
            } else if (operation == "RM") {
                RM(order);
            } else if (operation == "SM") {
                SM(order);
            } else if (operation == "LM") {
                LM(order);
            } else if (operation == "JP") {
                JP(order);
            } else if (operation == "JZ") {
                JZ(order);
            } else if (operation == "EX") {
                procesor.Scheduler();
            } else {
                System.out.println("Undefined order.");
            }
        }catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e);
            System.out.println("Incorrect order");
            SaveRegister();
            procesor.Scheduler();
        }
        SaveRegister();
        procesor.Scheduler();
    }
}
