package com.BamoOS.Modules.Interpreter;

import com.BamoOS.Modules.ACL.Interfaces.ILoginService;
import com.BamoOS.Modules.FileSystem.IFileSystem;
import com.BamoOS.Modules.MemoryManagment.PageTable;
import com.BamoOS.Modules.MemoryManagment.RAM;
import com.BamoOS.Modules.ProcessManager.IProcessManager;
import com.BamoOS.Modules.ProcessManager.PCB;
import com.BamoOS.Modules.Communication.IPC;
import com.BamoOS.Modules.Communication.Sms;
import com.BamoOS.Modules.Processor.IProcessor;

import java.util.ArrayList;

public class Interpreter implements IInterpreter{

    /**
     *
     Rozkazy:
     •AD reg1 reg2 - dodaje rejestr2 do rejestru1,
     •AX reg num – dodaje liczbę do rejestru, 
     //reg [address] - dodaje zawartość adresu do rejestru -->>> nie robi tego zarządzanie pamięcią,
     •SB reg1 reg2 - odejmuje od rejestru1 zawartość rejestru2, 
     •SX reg num – odejmuje liczbę od rejestru, 
     //reg [address] - odejmuje zawartość adresu od rejestru -->>> nie robi tego zarządzanie pamięcią,
     •DC reg - zwiększa zawartość rejestru o 1,
     •IC reg - zmniejsza zawartość rejestru o 1,
     •MU reg1 reg2 – mnoży rejestr 1 przez rejestr 2, 
     •MX reg num – mnoży rejestr przez liczbę, 
     //reg [address] - mnoży zawartość adresu razy zawartość rejestru -->>> nie robi tego zarządzanie pamięcią,
     •DV reg1 reg2 - dzieli zawartość rejestru1 przez zawartość rejestru2, 
     •DX reg num – dzieli rejestr od rejestru, 
     //reg [address] - dzieli zawartość rejestru przez liczbę z danej komórki -->>> nie robi tego zarządzanie pamięcią,
     •MD reg1 reg2 reg3 - reszta z dzielenia rejestru1 przez rejestr2 zapisywana 2 rejestrze3,
     •XM reg1 num reg2  - reszta z dzielenia rejestru1 przez liczbę zapisywana w rejestrze2,
     //reg1 [address] reg2 - reszta z dzielenia rejestru1 przez zawartość danej komórki zapisywana w rejestrze2 -->>> nie robi tego zarządzanie pamięcią,
     •MV reg1 reg2 – kopiuje wartość rejestru 2 do rejestru 1,
      •//MZ address reg - zapisuje do pamięci zawartość rejestru pod wskazanym adresem -->>> nie robi tego zarządzanie pamięcią,
     •MO reg n – umieszcza w rejestrze wartość n, 
     •//MY reg address - umieszcza w rejestrze zawartość pamiętaną pod wskazanym adresem -->>> nie robi tego zarządzanie pamięcią,
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
     •PP - wyświetla informacje o wszystkich procesach,
     •RM sender - zapisywanie otrzymanego komunikatu do RAM,
     •SM receiver message - wysłanie komunikatu, 
     •DM - czytanie komunikatów wysłanych procesów,
     •JP counter - skacze do innego rozkazu poprzez zmianę licznika, 
     •JZ reg n - skok przy zerowej zawartości rejestru będącego argumentem, 
     •PE - wyświetla wynik programu znajdujący się w rejestrze D,
     •EX - kończy program,
     **/

    private int A = 0;
    private int B = 0;
    private int C = 0;
    private int D = 0;
    private int PC = 0;
    private int PID = 0;

    private IProcessManager processManager;
    private RAM memory;
    private IFileSystem fileSystem;
    private IPC communication;
    private ILoginService loginService;

    Interpreter(RAM memory, IProcessManager processManager, IFileSystem fileSystem, IPC communication, ILoginService loginService) {
        this.procesor = procesor;
        this.memory = memory;
        this.processManager = processManager;
        this.fileSystem = fileSystem;
        this.communication = communication;
        this.loginService = loginService;
    }

    public void set_A(){
//        this.A = PCB.GetRegister(IPCB.Register.A);
        this.A = processManager.getActivePCB().getRegister(PCB.Register.A);
    }

    public void set_B(){
//        this.B = PCB.GetRegister(IPCB.Register.B);
        this.B = processManager.getActivePCB().getRegister(PCB.Register.B);
    }

    public void set_C(){
//        this.C = PCB.GetRegister(IPCB.Register.C);
        this.C = processManager.getActivePCB().getRegister(PCB.Register.C);
    }

    public void set_D(){
        this.D = processManager.getActivePCB().getRegister(PCB.Register.D);
    }

    public void set_PC(){
        this.PC = processManager.getActivePCB().getCounter();
    }

    public void set_PID(){
//        this.PID = PCB.GetPID();
        this.PID = processManager.getActivePCB().getPID();
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

    public int get_D(){
        return D;
    }

    public int get_PC(){
        return PC;
    }

    public int get_PID(){
        return PID;
    }

    private void DownloadRegisters(){
        set_A();
        set_B();
        set_C();
        set_D();
        set_PC();
        set_PID();
    }

    public void RegisterStatus() {
        System.out.println("PID: " + get_PID());
        System.out.println("Register A: " + get_A());
        System.out.println("Register B: " + get_B());
        System.out.println("Register C: " + get_C());
        System.out.println("Register D: " + get_D());
        System.out.println("Register PC: " + get_PC());
        System.out.println();
    }

    private void SaveRegister() {
//        PCB.SetRegister(IPCB.Register.A, get_A());
//        PCB.SetRegister(IPCB.Register.B, get_B());
//        PCB.SetRegister(IPCB.Register.C, get_C());
//        PCB.SetCounter(get_PC());
        processManager.getActivePCB().setRegister(PCB.Register.A, get_A());
        processManager.getActivePCB().setRegister(PCB.Register.B, get_B());
        processManager.getActivePCB().setRegister(PCB.Register.C, get_C());
        processManager.getActivePCB().setCounter(get_PC());
    }

    private void AD(String [] order){
        String reg_1 = order[1];
        String reg_2 = order[2];

        if (reg_1.equals("A")) {
            if (reg_2.equals("B")) {
                A += B;
            } else if (reg_2.equals("C")) {
                A += C;
            } else {
                System.out.println("Incorrect register.");
            }
        } else if (reg_1.equals("B")) {
            if (reg_2.equals("A")) {
                B += A;
            } else if (reg_2.equals("C")) {
                B += C;
            } else {
                System.out.println("Incorrect register.");
            }
        } else if (reg_1.equals("C")) {
            if (reg_2.equals("A")) {
                C += A;
            } else if (reg_2.equals("B")) {
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
        /*int len = order[2].length();

        if ((order[2].substring(0,1).equals("["))&&(order[2].substring(len-2,len-1)).equals("]")){
            String raw_address = order[2];
            raw_address = raw_address.replaceAll("\\[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);
//            PageTable programPageTable = memory.pageTables.get(processManager.getActivePCB().getPID());
//            char pom = memory.getCommand(PC,processManager.getActivePCB().getPID(), programPageTable);
            char pom = memory.readMemory(address);
            int address_content = (int) memory.readMemory(address);

            if (pom != '#' && Character.isDigit(pom)) {
                if (reg.equals("A")) {
                    A += address_content;
                } else if (reg.equals("B")) {
                    B += address_content;
                } else if (reg.equals("C")) {
                    C += address_content;
                } else {
                    System.out.println("Incorrect register.");
                }
            }else {
                System.out.println("Address is empty.");
            }
        }else if ((order[2].substring(0,1).equals("["))&&(order[2].substring(len-2,len-1).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((!order[2].substring(0,1).equals("["))&&(order[2].substring(len-2,len-1).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1).equals("["))&&(order[2].substring(1,2).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((!order[2].substring(0,1).equals("["))&&(!order[2].substring(len-2,len-1).equals("]"))){
           */ int val = Integer.parseInt(order[2]);

            if (reg.equals("A")) {
                A += val;
            } else if (reg.equals("B")) {
                B += val;
            } else if (reg.equals("C")) {
                C += val;
            } else {
                System.out.println("Incorrect register.");
            }
        }
    //}

    private void SB(String[] order){
        String reg_1 = order[1];
        String reg_2 = order[2];

        if (reg_1.equals("A")) {
            if (reg_2.equals("B")) {
                A -= B;
            } else if (reg_2.equals("C")) {
                A -= C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1.equals("B")) {
            if (reg_2.equals("A")) {
                B -= A;
            } else if (reg_2.equals("C")) {
                B -= C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1.equals("C")) {
            if (reg_2.equals("A")) {
                C -= A;
            } else if (reg_2.equals("B")) {
                C -= B;
            } else System.out.println("Incorrect register.");
        } else {
            System.out.println("Incorrect register.");
        }
    }

    private void SX(String[] order){
        String reg = order[1];
        /*int len = order[2].length();

        if ((order[2].substring(0,1).equals("["))&&(order[2].substring(len-2,len-1).equals("]"))){
            String raw_address = order[2];
            raw_address = raw_address.replaceAll("\\[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);
            char pom = memory.readMemory(address);
            int address_content = (int) memory.readMemory(address);

            if (pom != '#' && Character.isDigit(pom)) {

                if (reg.equals("A")) {
                    A -= address_content;
                } else if (reg.equals("B")) {
                    B -= address_content;
                } else if (reg.equals("C")) {
                    C -= address_content;
                } else {
                    System.out.println("Incorrect register.");
                }
            }else {
                System.out.println("Address is empty.");
            }
        }else if ((order[2].substring(0,1).equals("["))&&(!order[2].substring(len-2,len-1).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((!order[2].substring(0,1).equals("["))&&(order[2].substring(len-2,len-1).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1).equals("["))&&(order[2].substring(1,2).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((!order[2].substring(0,1).equals("["))&&(!order[2].substring(len-2,len-1).equals("]")){
            */
        int val = Integer.parseInt(order[2]);

            if (reg.equals("A")) {
                A -= val;
            } else if (reg.equals("B")) {
                B -= val;
            } else if (reg.equals("C")) {
                C -= val;
            } else {
                System.out.println("Incorrect register.");
            }
        }
    //}

    private void DC(String[] order){
        String reg = order[1];

        if (reg.equals("A")) {
            A -= 1;
        } else if (reg.equals("B")) {
            B -= 1;
        } else if (reg.equals("C")) {
            C -= 1;
        } else {
            System.out.println("Incorrect register.");
        }
    }

    private void IC(String[] order){
        String reg = order[1];
        if (reg.equals("A")) {
            A += 1;
        } else if (reg.equals("B")) {
            B += 1;
        } else if (reg.equals("C")) {
            C += 1;
        } else {
            System.out.println("Incorrect register.");
        }
    }

    private void MU(String[] order){
        String reg_1 = order[1];
        String reg_2 = order[2];

        if (reg_1.equals("A")) {
            if (reg_2.equals("B")) {
                A *= B;
            } else if (reg_2.equals("C")) {
                A *= C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1.equals("B")) {
            if (reg_2.equals("A")) {
                B *= A;
            } else if (reg_2.equals("C")) {
                B *= C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1.equals("C")) {
            if (reg_2.equals("A")) {
                C *= A;
            } else if (reg_2.equals("B")) {
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
        /*int len = order[2].length();

        if ((order[2].substring(0,1).equals("["))&&(order[2].substring(len-2,len-1).equals("]"))){
            String raw_address = order[2];
            raw_address = raw_address.replaceAll("\\[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);

            char pom = memory.readMemory(address);
            int address_content = (int) memory.readMemory(address);

            if (pom != '#' && Character.isDigit(pom)) {

                if (reg.equals("A")) {
                    A *= address_content;
                } else if (reg.equals("B")) {
                    B *= address_content;
                } else if (reg.equals("C")) {
                    C *= address_content;
                } else {
                    System.out.println("Incorrect register.");
                }
            }else {
                System.out.println("Address is empty.");
            }
        }else if ((order[2].substring(0,1).equals("["))&&(!order[2].substring(len-2,len-1).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((!order[2].substring(0,1).equals("["))&&(order[2].substring(len-2,len-1).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1).equals("["))&&(order[2].substring(1,2).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((!order[2].substring(0,1).equals("["))&&(!order[2].substring(len-2,len-1).equals("]"))){
          */  int val = Integer.parseInt(order[2]);

            if (reg.equals("A")) {
                A *= val;
            } else if (reg.equals("B")) {
                B *= val;
            } else if (reg.equals("C")) {
                C *= val;
            } else {
                System.out.println("Incorrect register.");
            }
        }
    //}

    private void DV(String[] order) {
        String reg_1 = order[1];
        String reg_2 = order[2];

        if (reg_1.equals("A")) {
            if (reg_2.equals("B")&& B!=0) {
                A /= B;
            } else if (reg_2.equals("C")&& C!=0) {
                A /= C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1.equals("B")) {
            if (reg_2.equals("A")&& A!=0) {
                B /= A;
            } else if (reg_2.equals("C")&& C!=0) {
                B /= C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1.equals("C")) {
            if (reg_2.equals("A") && A!=0) {
                C /= A;
            } else if (reg_2.equals("B") && B!=0) {
                C /= B;
            } else System.out.println("Incorrect register.");
        } else {
            System.out.println("Incorrect register.");
        }
    }

    private void DX(String[] order) {
        String reg = order[1];
        /*int len = order[2].length();

        if ((order[2].substring(0,1).equals("["))&&(order[2].substring(len-2,len-1).equals("]"))){
            String raw_address = order[2];
            raw_address = raw_address.replaceAll("\\[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);

            char pom = memory.readMemory(address);
            int addres_content = (int) memory.readMemory(address);

            if (pom != '#'&& addres_content!=0 && Character.isDigit(pom)) {
                if (reg.equals("A")) {
                    A /= addres_content;
                } else if (reg.equals("B")) {
                    B /= addres_content;
                } else if (reg.equals("C")) {
                    C /= addres_content;
                } else {
                    System.out.println("Incorrect register.");
                }
            }else if (pom == '#'){
                System.out.println("Address is empty.");
            }else if(addres_content == 0) {
                System.out.println("Not divide by zero.");
            }
        }else if ((order[2].substring(0,1).equals("["))&&(!order[2].substring(len-2,len-1).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((!order[2].substring(0,1).equals("["))&&(order[2].substring(len-2,len-1).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1).equals("["))&&(order[2].substring(1,2).equals("]"))){
            System.out.println("Incorrect address.");
        }else if ((!order[2].substring(0,1).equals("["))&&(!order[2].substring(len-2,len-1).equals("]"))){
            */int val = Integer.parseInt(order[2]);

            if (val != 0) {
                if (reg.equals("A")) {
                    A /= val;
                } else if (reg.equals("B")) {
                    B /= val;
                } else if (reg.equals("C")) {
                    C /= val;
                } else {
                    System.out.println("Incorrect register.");
                }
            }
        }
   // }

    private void MD(String[] order) {
        String reg_1 = order[1];
        String reg_2 = order[2];
        String reg_3 = order[3];

        if (reg_1.equals("A")) {
            if ((reg_2.equals("B")) && (reg_3.equals("A")) && B != 0) {
                A = A%B;
            } else if ((reg_2.equals("B") && (reg_3.equals("B")) && B != 0)) {
                B = A%B;
            } else if ((reg_2.equals("B") && (reg_3.equals("C")) && B != 0)) {
                C = A%B;
            } else if ((reg_2.equals("C") && (reg_3.equals("A")) && C != 0)){
                A = A%C;
            } else if (reg_2.equals("C") && (reg_3.equals("B")) && C != 0){
                B = A%C;
            } else if ((reg_2.equals("C") && (reg_3.equals("C")) && C != 0)){
                C = A%C;
            } else {
                System.out.println("Incorrect register.");
            }
        } else if (reg_1.equals("B")) {
            if ((reg_2.equals("A")) && (reg_3.equals("A")) && A != 0) {
                A = B%A;
            } else if ((reg_2.equals("A")) && (reg_3.equals("B")) && A != 0) {
                B = B%A;
            } else if ((reg_2.equals("A")) && (reg_3.equals("C")) && A != 0) {
                C = B%A;
            } else if ((reg_2.equals("C")) && (reg_3.equals("A")) && C != 0){
                A = B%C;
            } else if ((reg_2.equals("C"))&& (reg_3.equals("B")) && C != 0){
                B = B%C;
            } else if ((reg_2.equals("C")) && (reg_3.equals("C")) && C != 0){
                C = B%C;
            } else {
                System.out.println("Incorrect register.");
            }
        } else if (reg_1.equals("C")) {
            if ((reg_2.equals("A")) && (reg_3.equals("A")) && A != 0) {
                A = C%A;
            } else if ((reg_2.equals("A")) && (reg_3.equals("B")) && A != 0) {
                B = C%A;
            } else if ((reg_2.equals("A")) && (reg_3.equals("C")) && A != 0) {
                C = C%A;
            } else if ((reg_2.equals("C")) && (reg_3.equals("A")) && B != 0){
                A = C%B;
            } else if ((reg_2.equals("C")) && (reg_3.equals("B")) && B != 0){
                B = C%B;
            } else if ((reg_2.equals("C")) && (reg_3.equals("C")) && B != 0){
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

        /*int len = order[2].length();

        if ((order[2].substring(0,1).equals("["))&&(order[2].substring(len-2,len-1).equals("]"))){
            String raw_address = order[2];
            raw_address = raw_address.replaceAll("\\[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);

            char pom = memory.readMemory(address);
            int address_content = (int) memory.readMemory(address);

            if (pom != '#' && Character.isDigit(pom)) {
                if (reg_1.equals("A")) {
                    if (reg_3.equals("A")) {
                        A = A % address_content; }
                    if (reg_3.equals("B")) {
                        B = A % address_content; }
                    if (reg_3.equals("C")) {
                        C = A % address_content; }
                } else if (reg_1.equals("B")) {
                    if (reg_3.equals("A")) {
                        A = B % address_content; }
                    if (reg_3.equals("B")) {
                        B = B % address_content; }
                    if (reg_3.equals("C")) {
                        C = B % address_content; }
                } else if (reg_1.equals("C")) {
                    if (reg_3.equals("A")) {
                        A = C % address_content; }
                    if (reg_3.equals("B")) {
                        B = C % address_content; }
                    if (reg_3.equals("C")) {
                        C = C % address_content; }
                } else {
                    System.out.println("Incorrect register.");
                }
            }else {
                System.out.println("Address is empty.");
            }
        }else if ((order[2].substring(0,1).equals("["))&&(!order[2].substring(len-2,len-1).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((!order[2].substring(0,1).equals("["))&&(order[2].substring(len-2,len-1).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((order[2].substring(0,1).equals("["))&&(order[2].substring(1,2).equals("]"))) {
            System.out.println("Incorrect address.");
        }else if ((!order[2].substring(0,1).equals("["))&&(!order[2].substring(len-2,len-1).equals("]"))){
            */int val = Integer.parseInt(order[2]);

            if(val != 0) {
                if (reg_1.equals("A")) {
                    if (reg_3.equals("A")) {
                        A = A % val;
                    } else if (reg_3.equals("B")) {
                        B = A % val;
                    } else if (reg_3.equals("C")) {
                        C = A % val;
                    } else {
                        System.out.println("Incorrect register.");
                    }
                } else if (reg_1.equals("B")) {
                    if (reg_3.equals("A")) {
                        A = B % val;
                    } else if (reg_3.equals("B")) {
                        B = B % val;
                    } else if (reg_3.equals("C")) {
                        C = B % val;
                    } else {
                        System.out.println("Incorrect register.");
                    }
                } else if (reg_1.equals("C")) {
                    if (reg_3.equals("A")) {
                        A = C % val;
                    } else if (reg_3.equals("B")) {
                        B = C % val;
                    } else if (reg_3.equals("C")) {
                        C = C % val;
                    } else {
                        System.out.println("Incorrect register.");
                    }
                } else {
                    System.out.println("Incorrect register.");
                }
            }
        }

    //}

    private void MV(String[] order) {
        String reg_1 = order[1];
        String reg_2 = order[2];

        if (reg_1.equals("A")) {
            if (reg_2.equals("B")) {
                A = B;
            } else if (reg_2.equals("C")) {
                A = C;
            } else System.out.println("Incorrect register.");

        } else if (reg_1.equals("B")) {
            if (reg_2.equals("A")) {
                B = A;
            } else if (reg_2.equals("C")) {
                B = C;
            } else {
                System.out.println("Incorrect register.");
            }

        } else if (reg_1.equals("C")) {
            if (reg_2.equals("A")) {
                C = A;
            } else if (reg_2.equals("B")) {
                C = B;
            } else {
                System.out.println("Incorrect register.");
            }
        } else {
            System.out.println("Incorrect register.");
        }

    }

    /*private void MZ(String[] order) {
        String raw_address = order[1];
        String register = order[2];
        String[] split_address = raw_address.split("");

        if((!split_address[0].equals("["))|| (!split_address[raw_address.length()-1].equals("]"))){
            System.out.println("Incorrect address.");
        }else {
            raw_address = raw_address.replaceAll("\\[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);
            if (register.equals("A")) {
                memory.writeMemory((char) A, address);
            } else if(register.equals("B")){
                memory.writeMemory((char) B, address);
            } else if(register.equals("C")){
                memory.writeMemory((char) C, address);
            } else {
                System.out.println("Incorrect register.");
            }
        }
    }
    */

    private void MO(String[] order) {
        String reg = order[1];
        int val = Integer.parseInt(order[2]);

        if (reg.equals("A")) {
            A = val;
        } else if (reg.equals("B")) {
            B = val;
        } else if (reg.equals("C")) {
            C = val;
        } else {
            System.out.println("Incorrect register.");
        }
    }

   /* private void MY(String[] order) {
        String register = order[1];
        String raw_address = order[2];
        String[] split_address = raw_address.split("");

        if ((!split_address[0].equals("[")) || (!split_address[raw_address.length() - 1].equals("]"))) {
            System.out.println("Incorrect address.");
        } else {
            raw_address = raw_address.replaceAll("\\[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);
            char pom = memory.readMemory(address);

            if (pom != '#') {
                if (register.equals("A")) {
                    A = memory.readMemory(address);
                } else if (register.equals("B")) {
                    B = memory.readMemory(address);
                } else if (register.equals("C")) {
                    C = memory.readMemory(address);
                } else {
                    System.out.println("Incorrect register.");
                }
            } else {
                System.out.println("Address is empty.");
            }
        }
    }
    */

    private void CE(String[] order) {
            String filename = order[1];
            fileSystem.createFile(filename, loginService.getLoggedUser(),processManager);
    }

    private void CF(String[] order) {
        try {
            String filename = order[1];
            String fileContent = order[2];
            fileSystem.createFile(filename, loginService.getLoggedUser(), processManager);
            fileSystem.openFile(filename);
            fileSystem.appendFile(filename,fileContent);
            fileSystem.closeFile(filename);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void AF(String[] order) {
        try {
            String filename = order[1];
            String fileContent = order[2];
            fileSystem.openFile(filename);
            fileSystem.appendFile(filename, fileContent);
            fileSystem.closeFile(filename);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void DF(String[] order) {
        try {
            String filename = order[1];
            fileSystem.deleteFile(filename);
        } catch (Exception e) {
            throw;

        }
    }

    private void RF(String[] order) {
        try {
            String filename = order[1];
            fileSystem.openFile(filename);
            String fileContent = fileSystem.readFile(filename);
            fileSystem.closeFile(filename);
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
            String FileName = order[1];
            processManager.runNew(FileName);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void NG(String[] order) {
        try {
            String ProcessName = order[1];
            processManager.newProcessGroup(ProcessName);
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void KP(String[] order) {
        try {
            int PID = Integer.parseInt(order[1]);
            processManager.killProcess(PID);
        }catch (Exception e) {
            System.out.println(e);
        }
    }

    private void PP(String[] order){
        processManager.PrintProcesses();
    }

    private void SS(String[] order) {
        try {
            int PID = Integer.parseInt(order[1]);
            String state = order[2];
            if (state.equals("ACTIVE")) {
//                PCB.setState(PID, IPCB.State.ACTIVE);
                processManager.getActivePCB().setState(PCB.State.ACTIVE);
            } else if (state.equals("WAITING")) {
                processManager.getActivePCB().setState(PCB.State.WAITING);
            } else if (state.equals("READY")) {
                processManager.getActivePCB().setState(PCB.State.READY);
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void RP(String[] order) {
        try {
            int PID = Integer.parseInt(order[1]);
            processManager.runNew();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void KG(String[] order) {
        try {
            int PGID = Integer.parseInt(order[1]);
            processManager.killProcessGroup(PGID);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void RM(String[] order) {
        try {
            int PID = Integer.parseInt(order[1]);
            communication.receiveMessage();
        } catch (Exception e){
            System.out.println(e);
        }
    }

    private void SM(String[] order) {
        try {
            int PID = Integer.parseInt(order[1]);
            Sms sms = new Sms(order[2]);
            communication.sendMessage(PID, sms);
        } catch (Exception e){
            System.out.println(e);

        }
    }

    private void LM(String[] order) {
        communication.display_all();
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

            if (register.equals("A")) {
                if (A == 0) {
                    PC = counter;
                }
            } else if (register.equals("B")) {
                if (B == 0) {
                    PC = counter;
                }
            } else if (register.equals("C")) {
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

    public void Exe() throws Exception{
        String raw_order;
        String[] order = raw_order.split(" ");
        DownloadRegisters();
        RegisterStatus();

        try {
            String operation = order[0];

            if (operation.equals("AD")) {
                AD(order);
            } else if (operation.equals("AX")) {
                AX(order);
            } else if (operation.equals("SB")) {
                SB(order);
            } else if (operation.equals("SX")) {
                SX(order);
            } else if (operation.equals("DC")) {
                DC(order);
            } else if (operation.equals("IC")) {
                IC(order);
            } else if (operation.equals("MU")) {
                MU(order);
            } else if (operation.equals("MX")) {
                MX(order);
            } else if (operation.equals("DV")) {
                DV(order);
            } else if (operation.equals("DX")) {
                DX(order);
            } else if (operation.equals("MD")) {
                MD(order);
            } else if (operation.equals("XM")) {
                XM(order);
            } else if (operation.equals("MV")) {
                MV(order);
            /*} else if (operation.equals("MZ")) {
                MZ(order);
            */} else if (operation.equals("MO")) {
                MO(order);
            } else if (operation.equals("MY")) {
                MY(order);
            } else if (operation.equals("CE")) {
                CE(order);
            } else if (operation.equals("CF")) {
                CF(order);
            } else if (operation.equals("AF")) {
                AF(order);
            } else if (operation.equals("DF")) {
                DF(order);
            } else if (operation.equals("RF")) {
                RF(order);
            } else if (operation.equals("RN")) {
                RN(order);
            } else if (operation.equals("NP")) {
                NP(order);
            } else if (operation.equals("NG")) {
                NG(order);
            } else if (operation.equals("KP")) {
                KP(order);
            } else if (operation.equals("SS")) {
                SS(order);
            } else if (operation.equals("RP")) {
                RP(order);
            } else if (operation.equals("KG")) {
                KG(order);
            } else if (operation.equals("RM")) {
                RM(order);
            } else if (operation.equals("SM")) {
                SM(order);
            } else if (operation.equals("DM")) {
                LM(order);
            } else if (operation.equals("JP")) {
                JP(order);
            } else if (operation.equals("JZ")) {
                JZ(order);
            } else if (operation.equals("PE")){
                System.out.println("Result: " + get_D());
            } else if (operation.equals("EX")) {
                SaveRegister();
                processManager.getActivePCB().setState(PCB.State.FINISHED);
            } else {
                System.out.println("Undefined order.");
            }
        }catch (Exception e) {
            SaveRegister();
            throw;
        }
        PC++;
    }
}