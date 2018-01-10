package com.NachOS.Modules.Interpreter;

import com.NachOS.Modules.Exceptions.*;
import com.NachOS.Modules.FileSystem.File;
import com.NachOS.Modules.FileSystem.FileSystem;
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
        CP name file_name - tworzenie procesu o podanej nazwie,
        KP PID - usunięcie procesu po ID,

        Pliki
        CE file_name - tworzy pusty plik o podanej nazwie,
        OF file_name - otwiera plik o podanej nazwie
        CF file_name - zamyka plik o podanej nazwie,
        AF file_name file_content - dodaje dane na końcu pliku,
        DF file_name - usuwa plik o danej nazwie,
        RF file_name - czyta plik o podanej nazwie,
        RN old_file_name new_file_name - zmienia nazwę pliku

        Komunikaty
        RM  - zapisywanie otrzymanego komunikatu do RAM,
        SM  - wysłanie komunikatu 
        LM - wczytywanie i wysyłanie wiadomości z RAM,

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
        //SaveTimer();
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
        //SaveTimer();
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
        //SaveTimer();
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
        //SaveTimer();
    }

    //DC reg - zmniejsza zawartość rejestru o 1
    private void DC(String[] order, int A, int B, int C, int PC) throws Exception {
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
        //SaveTimer();
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
        //SaveTimer();
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
        //SaveTimer();
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
        //SaveTimer();
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
        //SaveTimer();
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
        //SaveTimer();
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
        //SaveTimer();
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
        //SaveTimer();
    }

    //MZ address reg - zapisuje do pamięci zawartość rejestru pod wskazanym adresem,
    private void MZ(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg = order[2];
        String bAddress = order[1];

        int lenbAddress = bAddress.length();

        char left = bAddress.charAt(0);
        char right = bAddress.charAt(lenbAddress-1);

        if((left == '[')||((right == ']'))){
            throw new Exception("Nieprawidlowy adres");
        }
        String logicalAddress = bAddress.replaceAll("\\[", "").replaceAll("]", "");
        int Address = Integer.parseInt(logicalAddress);

        switch (reg){
            case "A":
                processManager.setSafeMemory(Address, (char) A);
                break;
            case "B":
                processManager.setSafeMemory(Address, (char) B);
                break;
            case "C":
                processManager.setSafeMemory(Address, (char) C);
                break;
            default:
                throw new IncorrectRegisterException("Nieprawidlowy rejestr");
        }

        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        //SaveTimer();
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
        //SaveTimer();
    }

    //MY reg address - umieszcza w rejestrze zawartość pamiętaną pod wskazanym adresem,
    private void MY(String[] order, int A, int B, int C, int PC) throws Exception {
        String reg = order[1];
        String bAddress = order[2];

        int lenbAddress = bAddress.length();

        char left = bAddress.charAt(0);
        char right = bAddress.charAt(lenbAddress-1);

        if((left == '[')||((right == ']'))){
            throw new Exception("Nieprawidlowy adres");
        }
        String logicalAddress = bAddress.replaceAll("\\[", "").replaceAll("]", "");
        int Address = Integer.parseInt(logicalAddress);

        switch (reg){
            case "A":
                    A = processManager.getSafeMemory(Address);
                break;
            case "B":
                    B = processManager.getSafeMemory(Address);
                    break;
                case "C":
                    C = processManager.getSafeMemory(Address);
                    break;
                default:
                    throw new IncorrectRegisterException("Nieprawidlowy rejestr");
            }
        PC++;
        RegisterStatus(A,B,C,PC);
        SaveRegister(A,B,C,PC);
        //SaveTimer();
    }

    //JP counter - skacze do innego rozkazu poprzez zmianę licznika
    private void JP(String[] order, int A, int B, int C, int PC) {
        int counter = Integer.parseInt(order[1]);
        PC = counter;
        processManager.getActivePCB().setCounter(PC);
        RegisterStatus(A,B,C,PC);
        //SaveTimer();
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
        //SaveTimer();
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
        //SaveTimer();
        }

    //---------------------------------PROCESY---------------------------------------

    //CP file_name - tworzenie procesu o podanej nazwie i nazwie pliku
    private void CP(String[] order, int PC) throws Exception {
        String name = order[1];
        String fileName = order[2];
        try {
            processManager.runNew(name, fileName);
        } catch (Exception e){
            throw e;
        }
        PC++;
        processManager.getActivePCB().setCounter(PC);
        //SaveTimer();
    }

    //KP file_name - usunięcie procesu po ID
    private void KP(String[] order, int PC) throws Exception {
        int PID = Integer.parseInt(order[1]);
        try {
            processManager.killProcess(PID);
        } catch (Exception e){
            throw e;
        }
        PC++;
        processManager.getActivePCB().setCounter(PC);
        //SaveTimer();
    }

    //-----------------------------------PLIKI---------------------------------------

    //CE file_name - tworzy pusty plik o podanej nazwie
    private void CE(String[] order, int PC) throws Exception {
        try {
            String filename = order[1];
            fileSystem.createFile(filename, loginService.getLoggedUser(), processManager);
        } catch (FileSystemException e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            //SaveTimer();
        }
    }

    //OF file_name - otwiera plik o podanej nazwie
    private void OF(String[] order, int PC) throws Exception {
        try {
            String filename = order[1];
            fileSystem.openFile(filename);
        } catch (ChangedToWaitingException e){
            PC--;
        } catch (FileSystemException e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            //SaveTimer();
        }
    }

    //CL file_name - zamyka plik o podanej nazwie
    private void CF(String[] order, int PC) throws Exception {
        try {
            String filename = order[1];
            fileSystem.closeFile(filename);
        } catch (FileSystemException e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            //SaveTimer();
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
        } catch (FileSystemException e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            //SaveTimer();
        }
    }

    //DF file_name - usuwa plik o danej nazwie
    private void DF(String[] order, int PC) throws Exception {
        try {
            String filename = order[1];
            fileSystem.deleteFile(filename);
        } catch (FileSystemException e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            //SaveTimer();
        }
    }

    //RF file_name - czyta plik o podanej nazwie
    private void RF(String[] order, int PC) throws Exception {
        try {
            String filename = order[1];
            String fileContent = fileSystem.readFile(filename);
            System.out.println("File content:");
            System.out.println(fileContent);
        } catch (FileSystemException e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            //SaveTimer();
        }
    }

    //RN old_file_name new_file_name - zmienia nazwę pliku
    private void RN(String[] order, int PC) throws Exception {
        try {
            String oldName = order[1];
            String newName = order[2];
            fileSystem.renameFile(oldName, newName);
        } catch (FileSystemException e) {
            throw e;
        } finally {
            PC++;
            processManager.getActivePCB().setCounter(PC);
            //SaveTimer();
        }
    }

    //-----------------------------KOMUNIKATY---------------------------------------

    //RM - zapisywanie otrzymanego komunikatu do RAM,
    private void RM(String[] order, int PC) throws Exception {
        String bAddress = order[1];

        int lenbAddress = bAddress.length();

        char left = bAddress.charAt(0);
        char right = bAddress.charAt(lenbAddress-1);

        if((left == '[')||((right == ']'))){
            throw new Exception("Nieprawidlowy adres");
        }

        String logicalAddress = bAddress.replaceAll("\\[", "").replaceAll("]", "");
        int Address = Integer.parseInt(logicalAddress);

        //Jeśli złapie ChangedToWaitingException to licznik się nie zmienia
        try {
            communication.receiveMessage(Address);
        } catch (ChangedToWaitingException e) {
            PC--;
        }
        PC++;
        processManager.getActivePCB().setCounter(PC);
        //SaveTimer();
    }

    //SM - wysłanie komunikatu
    private void SM(String[] order, int PC) {
        int PID = Integer.parseInt(order[1]);
        Sms sms = new Sms(order[2]);
        communication.sendMessage(PID, sms);
        PC++;
        processManager.getActivePCB().setCounter(PC);
        //SaveTimer();
    }

    //LM - wczytywanie i wysyłanie wiadomości z RAM
    private void LM(String[] order, int PC) {
        String s_PID = order[1];
        String bAddress = order[2];

        int PID = Integer.parseInt(s_PID);
        int address = Integer.parseInt(bAddress);

        try {
            communication.loadAndSend(PID, address);
        } catch (Exception e){
            throw e;
        }

        PC++;
        processManager.getActivePCB().setCounter(PC);
        //SaveTimer();
    }

    //------------------------------------------------------------------------------

    //EX - kończy program
    private void EX(int PC){
        processManager.setStateOfActivePCB(PCB.State.FINISHED);
        PC++;
        //SaveTimer();
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
                case "MD":
                    MD(order, A, B, C, PC);
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
                case "LM":
                    LM(order, PC);
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
