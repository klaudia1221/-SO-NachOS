package com.NachOS.Modules.Interpreter;

import com.NachOS.Modules.Exceptions.ChangedToWaitingException;
import com.NachOS.Modules.Exceptions.DivideZeroException;
import com.NachOS.Modules.Exceptions.IncorrectRegisterException;
import com.NachOS.Modules.Exceptions.UndefinedOrderException;
import com.NachOS.Modules.ProcessManager.IProcessManager;
import com.NachOS.Modules.FileSystem.IFileSystem;
import com.NachOS.Modules.Communication.IPC;
import com.NachOS.Modules.Communication.Sms;
import com.NachOS.Modules.ACL.Interfaces.ILoginService;
import com.NachOS.Modules.ProcessManager.PCB;
import com.NachOS.Modules.MemoryManagment.RAM;

public class Interpreter implements IInterpreter {
    /**
     Rozkazy:
        Arytmetyczno-logiczne
        AD reg1 reg2 - dodaje rejestr2 do rejestru1,
        AX reg num – dodaje liczbę do rejestru,
        SB reg1 reg2 - odejmuje od rejestru1 zawartość rejestru2,
        SX reg num – odejmuje liczbę od rejestru,
        DC reg - zmniejsza zawartość rejestru o 1,
        IC reg - zwiększa zawartość rejestru o 1,
        MU reg1 reg2 – mnoży rejestr 1 przez rejestr 2,
        MX reg num – mnoży rejestr przez liczbę,
        DV reg1 reg2 - dzieli zawartość rejestru1 przez zawartość rejestru2,
        DX reg num – dzieli rejestr przez liczbę,
        MD reg1 val reg2 - reszta z dzielenia reg1 przez val zapisywany do reg2
        MV reg1 reg2 – kopiuje zawartość rejestru 2 do rejestru 1,
        MZ address reg - zapisuje do pamięci zawartość rejestru pod wskazanym adresem,
        MO reg n – umieszcza w rejestrze wartość n,
        MY reg address - umieszcza w rejestrze zawartość pamiętaną pod wskazanym adresem,
        JP counter - skacze do innego rozkazu poprzez zmianę licznika,
        JZ reg n - skok przy zerowej zawartości rejestru będącego argumentem,
        PE reg - wyświetla wynik programu znajdujący się w podanym rejestrze,

        Procesy
        CP file_name - tworzenie procesu o podanej nazwie,
        KP file_name - usunięcie procesu po ID,
        RP file_name – uruchamia proces o podanym ID

        Pliki
        CE file_name - tworzy pusty plik o podanej nazwie,
        OF file_name - otwiera plik o podanej nazwie
        CL file_name - zamyka plik o podanej nazwie,
        CF file_name file_content - tworzy plik z zawartością,
        AF file_name file_content - dodaje dane na końcu pliku,
        DF file_name - usuwa plik o danej nazwie,
        RF file_name - czyta plik o podanej nazwie,
        RN old_file_name new_file_name - zmienia nazwę pliku

        Komunikaty
        RM  - zapisywanie otrzymanego komunikatu do RAM,
        SM  - wysłanie komunikatu 

        EX - kończy program
     **/

    private IProcessManager processManager;
    private IFileSystem fileSystem;
    private IPC communication;
    private ILoginService loginService;
    private RAM memory;

    public Interpreter(IProcessManager processManager,
                       IFileSystem fileSystem,
                       IPC communication,
                       ILoginService loginService) {
        this.processManager = processManager;
        this.fileSystem = fileSystem;
        this.communication = communication;
        this.loginService = loginService;
    }

    private void RegisterStatus(int A, int B, int C, int PC) {
        System.out.println("Register A: " + A);
        System.out.println("Register B: " + B);
        System.out.println("Register C: " + C);
        System.out.println("Register PC: " + PC);
        System.out.println();
    }

    private void SaveRegister(int A, int B, int C, int PC) {
        processManager.getActivePCB().setRegister(PCB.Register.A, A);
        processManager.getActivePCB().setRegister(PCB.Register.B, B);
        processManager.getActivePCB().setRegister(PCB.Register.C, C);
        processManager.getActivePCB().setCounter(PC);
    }

    private void SaveTimer() {
        int counter = processManager.getActivePCB().getTimer();
        counter++;
        processManager.getActivePCB().setTimer(counter);
    }

    //------------------------ARYTMETYCZNO-LOGICZNE----------------------------------

    //AD reg1 reg2 - dodaje rejestr2 do rejestru1
    private void AD(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg_1 = order[1];
        String reg_2 = order[2];

        switch (reg_1) {
            case "A":
                switch (reg_2) {
                    case "B":
                        A += B;
                        break;
                    case "C":
                        A += C;
                        break;
                    default:
                        throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                }
                break;
            case "B":
                switch (reg_2) {
                    case "A":
                        B += A;
                        break;
                    case "C":
                        B += C;
                        break;
                    default:
                        throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                }
                break;
            case "C":
                switch (reg_2) {
                    case "A":
                        C += A;
                        break;
                    case "B":
                        C += B;
                        break;
                    default:
                        throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                }
                break;
            default:
                throw new IncorrectRegisterException("Nieprawidlowy rejestr");
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //AX reg num – dodaje liczbę do rejestru
    private void AX(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg = order[1];
        int val = Integer.parseInt(order[2]);

        switch (reg) {
            case "A":
                A += val;
                break;
            case "B":
                B += val;
                break;
            case "C":
                C += val;
                break;
            default:
                throw new IncorrectRegisterException("Nieprawidlowy rejestr");
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //SB reg1 reg2 - odejmuje od rejestru1 zawartość rejestru2
    private void SB(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg_1 = order[1];
        String reg_2 = order[2];

        switch (reg_1) {
            case "A":
                switch (reg_2) {
                    case "B":
                        A -= B;
                        break;
                    case "C":
                        A -= C;
                        break;
                    default:
                        throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                }
                break;
            case "B":
                switch (reg_2) {
                    case "A":
                        B -= A;
                        break;
                    case "C":
                        B -= C;
                        break;
                    default:
                        throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                }
                break;
            case "C":
                switch (reg_2) {
                    case "A":
                        C -= A;
                        break;
                    case "B":
                        C -= B;
                        break;
                    default:
                        throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                }
                break;
            default:
                throw new IncorrectRegisterException("Nieprawidlowy rejestr");
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //SX reg num – odejmuje liczbę od rejestru
    private void SX(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg = order[1];
        int val = Integer.parseInt(order[2]);

        switch (reg) {
            case "A":
                A -= val;
                break;
            case "B":
                B -= val;
                break;
            case "C":
                C -= val;
                break;
            default:
                throw new IncorrectRegisterException("Nieprawidlowy rejestr");
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //DC reg - zmniejsza zawartość rejestru o 1
    private void DC(String[] order, int A, int B, int C, int PC) throws Exception{
        String reg = order[1];

        switch (reg) {
            case "A":
                A -= 1;
                break;
            case "B":
                B -= 1;
                break;
            case "C":
                C -= 1;
                break;
            default:
                throw new IncorrectRegisterException("Nieprawidlowy rejestr");
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //IC reg - zwiększa zawartość rejestru o 1
    private void IC(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg = order[1];

        switch (reg) {
            case "A":
                A += 1;
                break;
            case "B":
                B += 1;
                break;
            case "C":
                C += 1;
                break;
            default:
                throw new IncorrectRegisterException("Nieprawidlowy rejestr");
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //MU reg1 reg2 – mnoży rejestr 1 przez rejestr 2
    private void MU(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg_1 = order[1];
        String reg_2 = order[2];

        switch (reg_1) {
            case "A":
                switch (reg_2) {
                    case "B":
                        A *= B;
                        break;
                    case "C":
                        A *= C;
                        break;
                    default:
                        throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                }
                break;
            case "B":
                switch (reg_2) {
                    case "A":
                        B *= A;
                        break;
                    case "C":
                        B *= C;
                        break;
                    default:
                        throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                }
                break;
            case "C":
                switch (reg_2) {
                    case "A":
                        C *= A;
                        break;
                    case "B":
                        C *= B;
                        break;
                    default:
                        throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                }
                break;
            default:
                throw new IncorrectRegisterException("Nieprawidlowy rejestr");
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //MX reg num – mnoży rejestr przez liczbę
    private void MX(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg = order[1];
        int val = Integer.parseInt(order[2]);

        switch (reg) {
            case "A":
                A *= val;
                break;
            case "B":
                B *= val;
                break;
            case "C":
                C *= val;
                break;
            default:
                throw new IncorrectRegisterException("Nieprawidlowy rejestr");
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //DV reg1 reg2 - dzieli zawartość rejestru1 przez zawartość rejestru2
    private void DV(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg_1 = order[1];
        String reg_2 = order[2];

        if (reg_2 == "0"){
            throw new DivideZeroException("Dzielenie przez zero");
        } else {
            switch (reg_1) {
                case "A":
                    switch (reg_2) {
                        case "B":
                            A /= B;
                            break;
                        case "C":
                            A /= C;
                            break;
                        default:
                            throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                    }
                    break;
                case "B":
                    switch (reg_2) {
                        case "A":
                            B /= A;
                            break;
                        case "C":
                            B /= C;
                            break;
                        default:
                            throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                    }
                    break;
                case "C":
                    switch (reg_2) {
                        case "A":
                            C /= A;
                            break;
                        case "B":
                            C /= B;
                            break;
                        default:
                            throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                    }
                    break;
                default:
                    throw new IncorrectRegisterException("Nieprawidlowy rejestr");
            }
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //DX reg num – dzieli rejestr przez liczbę
    private void DX(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg = order[1];
        int val = Integer.parseInt(order[2]);

        if (val == 0) {
            throw new DivideZeroException("Dzielenie przez zero.");
        } else {
            switch (reg) {
                case "A":
                    A /= val;
                    break;
                case "B":
                    B /= val;
                    break;
                case "C":
                    C /= val;
                    break;
                default:
                    throw new IncorrectRegisterException("Nieprawidlowy rejestr");
            }
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //DM reg1 val reg2 - reszta z dzielenia reg1 przez val zapisywany do reg2
    private void MD(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg_1 = order[1];
        int val = Integer.parseInt(order[2]);
        String reg_2 = order[3];

        if(val == 0){
            throw new DivideZeroException("Dzielenie przez zero");
        }
        else {
            switch (reg_1) {
                case "A":
                    switch (reg_2){
                        case "A":
                            A = A % val;
                            break;
                        case "B":
                            B = A % val;
                            break;
                        case "C":
                            C = A % val;
                            break;
                        default:
                            throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                    }
                    break;
                case "B":
                    switch (reg_2){
                        case "A":
                            A = B % val;
                            break;
                        case "B":
                            B = B % val;
                            break;
                        case "C":
                            C = B % val;
                            break;
                        default:
                            throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                    }
                    break;
                case "C":
                    switch (reg_2){
                        case "A":
                            A = C % val;
                            break;
                        case "B":
                            B = C % val;
                            break;
                        case "C":
                            C = C % val;
                            break;
                        default:
                            throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                    }
                    break;
                default:
                    throw new IncorrectRegisterException("Nieprawidlowy rejestr");
            }
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //MV reg1 reg2 – kopiuje zawartość rejestru 2 do rejestru 1
    private void MV(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg_1 = order[1];
        String reg_2 = order[2];

        switch (reg_1) {
            case "A":
                switch (reg_2) {
                    case "B":
                        A = B;
                        break;
                    case "C":
                        A = C;
                        break;
                    default:
                        throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                }
                break;
            case "B":
                switch (reg_2) {
                    case "A":
                        B = A;
                        break;
                    case "C":
                        B = C;
                        break;
                    default:
                        throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                }
                break;
            case "C":
                switch (reg_2) {
                    case "A":
                        C = A;
                        break;
                    case "B":
                        C = B;
                        break;
                    default:
                        throw new IncorrectRegisterException("Nieprawidlowy rejestr");
                }
                break;
            default:
                throw new IncorrectRegisterException("Nieprawidlowy rejestr");
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //MZ address reg - zapisuje do pamięci zawartość rejestru pod wskazanym adresem,
    //TODO metoda do zapisywania do pamięci - Klaudia
    private void MZ(String[] order, int A, int B, int C, int PC) throws Exception {
        String raw_address = order[1];
        String reg = order[2];

        String[] split_address = raw_address.split("");

        int len = raw_address.length();

        String left = split_address[0];
        String right = split_address[len-1];

        String stringA = Integer.toString(A);
        int lenStringA = stringA.length();

        int PID = processManager.getActivePCB().getPID();

        if((left == "[") || (right == "]")){
            throw new Exception("Nieprawidlowy adres");
        }
        else {
            raw_address = raw_address.replaceAll("\\[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);
            switch (reg){
                case "A":
                    for (int i=0; i<lenStringA; i++) {
                        //memory.writeCharToRam(PID, address, stringA[i]);
                        address++;
                    }
                    break;
                case "B":
                    //memory.writeMemory((char) B, address);
                    break;
                case "C":
                    //memory.writeMemory((char) C, address);
                    break;
                default:
                    throw new IncorrectRegisterException("Nieprawidlowy rejestr");
            }
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //MO reg n – umieszcza w rejestrze wartość n,
    private void MO(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg = order[1];
        int val = Integer.parseInt(order[2]);

        switch (reg) {
            case "A":
                A = val;
                break;
            case "B":
                B = val;
                break;
            case "C":
                C = val;
                break;
            default:
                throw new IncorrectRegisterException("Nieprawidlowy rejestr");
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //MY reg address - umieszcza w rejestrze zawartość pamiętaną pod wskazanym adresem,
    //TODO metoda do czytania z pamięci - Klaudia
    private void MY(String[] order, int A, int B, int C, int PC) {
        String reg = order[1];
        String raw_address = order[2];
        String[] split_address = raw_address.split("");

        if ((!split_address[0].equals("[")) || (!split_address[raw_address.length() - 1].equals("]"))) {
            System.out.println("Incorrect address.");
        } else {
            raw_address = raw_address.replaceAll("\\[", "").replaceAll("]", "");
            int address = Integer.parseInt(raw_address);
            /*
            char pom = memory.readMemory(address);

            if (pom != '#') {
                switch (reg) {
                    case "A":
                        A = memory.readMemory(address);
                        break;
                    case "B":
                        B = memory.readMemory(address);
                        break;
                    case "C":
                        C = memory.readMemory(address);
                        break;
                    default:
                        System.out.println("Incorrect register.");
                        break;
                }
            } else {
                System.out.println("Address is empty.");
            }
            */
        }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        SaveTimer();
    }

    //JP counter - skacze do innego rozkazu poprzez zmianę licznika
    private void JP(String[] order, int A, int B, int C, int PC) {
        int counter = Integer.parseInt(order[1]);
        PC = counter;
        processManager.getActivePCB().setCounter(PC);
        RegisterStatus(A,B,C,PC);
        SaveTimer();
    }

    //JZ reg n - skok przy zerowej zawartości rejestru będącego argumentem,
    private void JZ(String[] order, int A, int B, int C, int PC) throws Exception {
            String reg = order[1];
            int counter = Integer.parseInt(order[2]);

            switch (reg){
                case "A":
                    if (A == 0) {
                        PC = counter;
                    } else {
                        PC++;
                    }
                    break;
                case "B":
                    if (B == 0) {
                        PC = counter;
                    } else {
                        PC++;
                    }
                    break;
                case "C":
                    if (C == 0) {
                        PC = counter;
                    } else {
                        PC++;
                    }
                    break;
                default:
                    throw new IncorrectRegisterException("Nieprawidlowy rejestr");
            }
        processManager.getActivePCB().setCounter(PC);
        RegisterStatus(A,B,C,PC);
        SaveTimer();
    }

    //PE reg - wyświetla wynik programu znajdujący się w podanym rejestrze,
    private void PE(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg = order[1];
        switch (reg) {
            case "A":
                System.out.println("Result: " + A);
                break;
            case "B":
                System.out.println("Result: " + B);
                break;
            case "C":
                System.out.println("Result: " + C);
                break;
            default:
                throw new IncorrectRegisterException("Nieprawidlowy rejestr");
        }
        PC++;
        SaveRegister(A,B,C,PC);
        SaveTimer();
        }

    //---------------------------------PROCESY---------------------------------------

    //CP file_name - tworzenie procesu o podanej nazwie i nazwie pliku
    private void CP(String[] order, int PC) throws Exception{
        String name = order[1];
        String fileName = order[2];
        try {
            processManager.runNew(name, fileName);
        } catch (Exception e){
            throw e;
        }
        PC++;
        processManager.getActivePCB().setCounter(PC);
        SaveTimer();
    }

    //TODO ogarnąć
    //KP file_name - usunięcie procesu po ID,
    //wykorzystywane przy komunikatach
    private void KP(String[] order, int PC) throws Exception{
        int PID = Integer.parseInt(order[2]);
        try {
            processManager.killProcess(PID);
        } catch (Exception e){
            throw e;
        }
        PC++;
        processManager.getActivePCB().setCounter(PC);
        SaveTimer();
    }

    //-----------------------------------PLIKI---------------------------------------

    //CE file_name - tworzy pusty plik o podanej nazwie
    private void CE(String[] order, int PC) throws Exception {
        try {
            String filename = order[1];
            fileSystem.createFile(filename, loginService.getLoggedUser(), processManager);
        } catch (Exception e) {

            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            SaveTimer();
        }
    }

    //OF file_name - otwiera plik o podanej nazwie
    private void OF(String[] order, int PC) throws Exception {
        try {
            String filename = order[1];
            fileSystem.openFile(filename);
        } catch (ChangedToWaitingException e){
            PC--;
        } catch (Exception e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            SaveTimer();
        }
    }

    //CL file_name - zamyka plik o podanej nazwie
    private void CL(String[] order, int PC) throws Exception {
        try {
            String filename = order[1];
            fileSystem.closeFile(filename);
        } catch (Exception e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            SaveTimer();
        }
    }

    //CF file_name file_content - tworzy plik z zawartością
    private void CF(String[] order, int PC) throws Exception {
        try {
            String filename = order[1];
            int n = order.length;

            String fileContent = "";
            for (int i = 2; i < n; i++) {
                fileContent += order[i];
            }

            fileSystem.createFile(filename, loginService.getLoggedUser(), processManager);
            fileSystem.openFile(filename);
            fileSystem.appendFile(filename, fileContent);
            fileSystem.closeFile(filename);
        } catch (Exception e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            SaveTimer();
        }
    }

    //AF file_name file_content - dodaje dane na końcu pliku
    private void AF(String[] order, int PC) throws Exception {
        try {
            String filename = order[1];
            int n = order.length;

            String fileContent = "";
            for (int i = 2; i < n; i++) {
                fileContent += order[i];
            }
            fileSystem.appendFile(filename, fileContent);
        } catch (Exception e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            SaveTimer();
        }
    }

    //DF file_name - usuwa plik o danej nazwie
    private void DF(String[] order, int PC) throws Exception {
        try {
            String filename = order[1];
            fileSystem.deleteFile(filename);
        } catch (Exception e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            SaveTimer();
        }
    }

    //RF file_name - czyta plik o podanej nazwie
    private void RF(String[] order, int PC) throws Exception {
        try {
            String filename = order[1];
            String fileContent = fileSystem.readFile(filename);
            System.out.println("File content:");
            System.out.println(fileContent);
        } catch (Exception e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            SaveTimer();
        }
    }

    //RN old_file_name new_file_name - zmienia nazwę pliku
    private void RN(String[] order, int PC) throws Exception {
        try {
            String oldName = order[1];
            String newName = order[2];
            fileSystem.renameFile(oldName, newName);
        } catch (Exception e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            SaveTimer();
        }
    }

    //-----------------------------KOMUNIKATY---------------------------------------

    //RM  - zapisywanie otrzymanego komunikatu do RAM
    //Kuba metoda receiveMessage
    private void RM(String[] order, int PC) {
        String bAddress = order[1];

        int len = bAddress.length();

        char left = bAddress.charAt(0);
        char right = bAddress.charAt(len-1);

        String logicalAddress ="";

        for(int i=1;i<len-1;i++){
            logicalAddress += bAddress.charAt(i);
        }

        int Address = Integer.parseInt(logicalAddress);

        /*
        if((left == "[")||(right == ("]")){
            throw new Exception("Nieprawidlowy adres");
        }
        */

        String message = "";
        for (int i = 1; i < len; i++) {
            message += order[i];
        }
        //Jeśli złapie ChangedToWaitingException to licznik się nie zmienia
        try {
            communication.receiveMessage(Address);
        } catch (ChangedToWaitingException e) {
            PC--;
        }
        PC++;
        processManager.getActivePCB().setCounter(PC);
        SaveTimer();
    }

    //SM  - wysłanie komunikatu
    //Kuba metoda sendMessage
    private void SM(String[] order, int PC) {
        int PID = Integer.parseInt(order[1]);
        Sms sms = new Sms(order[2]);
        communication.sendMessage(PID, sms);
        PC++;
        processManager.getActivePCB().setCounter(PC);
        SaveTimer();
    }

    //------------------------------------------------------------------------------

    //EX - kończy program
    private void EX(int PC){
        processManager.setStateOfActivePCB(PCB.State.FINISHED);
        PC++;
        SaveTimer();
    }

    //------------------------------------------------------------------------------

    public void Exe() throws Exception {
        int A, B, C, PC;
        try {
            A = processManager.getActivePCB().getRegister(PCB.Register.A);
            B = processManager.getActivePCB().getRegister(PCB.Register.B);
            C = processManager.getActivePCB().getRegister(PCB.Register.C);
            PC = processManager.getActivePCB().getCounter();

            String raw_order = processManager.getCommand(PC);
            String[] order = raw_order.split(" ");
            String operation = order[0];

            switch (operation) {
                //------------------------ARYTMETYCZNO-LOGICZNE----------------------------------
                case "AD":
                    AD(order, A, B, C, PC);
                    break;
                case "AX":
                    AX(order, A, B, C, PC);
                    break;
                case "SB":
                    SB(order, A, B, C, PC);
                    break;
                case "SX":
                    SX(order, A, B, C, PC);
                    break;
                case "DC":
                    DC(order, A, B, C, PC);
                    break;
                case "IC":
                    IC(order, A, B, C, PC);
                    break;
                case "MU":
                    MU(order, A, B, C, PC);
                    break;
                case "MX":
                    MX(order, A, B, C, PC);
                    break;
                case "DV":
                    DV(order, A, B, C, PC);
                    break;
                case "DX":
                    DX(order, A, B, C, PC);
                    break;
                case "MV":
                    MV(order, A, B, C, PC);
                    break;
                case "MZ":
                    MZ(order, A, B, C, PC);
                    break;
                case "MO":
                    MO(order, A, B, C, PC);
                    break;
                case "MY":
                    MY(order, A, B, C, PC);
                    break;
                case "JP":
                    JP(order, A, B, C, PC);
                    break;
                case "JZ":
                    JZ(order, A, B, C, PC);
                    break;
                case "PE":
                    PE(order, A, B, C, PC);
                    break;
                //---------------------------------PROCESY---------------------------------------
                case "CP":
                    CP(order, PC);
                    break;
                case "KP":
                    KP(order, PC);
                    break;
                //-----------------------------------PLIKI---------------------------------------
                case "CE":
                    CE(order, PC);
                    break;
                case "OF":
                    OF(order, PC);
                    break;
                case "CL":
                    CL(order, PC);
                    break;
                case "CF":
                    CF(order, PC);
                    break;
                case "AF":
                    AF(order, PC);
                    break;
                case "DF":
                    DF(order, PC);
                    break;
                case "RF":
                    RF(order, PC);
                    break;
                case "RN":
                    RN(order, PC);
                    break;
                //-----------------------------KOMUNIKATY---------------------------------------
                case "RM":
                    RM(order, PC);
                    break;
                case "SM":
                    SM(order, PC);
                    break;
                case "EX":
                    EX(PC);
                    break;
                default:
                   throw new UndefinedOrderException("Niepoprawny rozkaz");
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
