package com.BamoOS.Modules.Shell;

import com.BamoOS.Modules.ACL.Group;
import com.BamoOS.Modules.ACL.Interfaces.IACLController;
import com.BamoOS.Modules.ACL.Interfaces.ILoginService;
import com.BamoOS.Modules.ACL.Interfaces.IUserController;
import com.BamoOS.Modules.ACL.Mask;
import com.BamoOS.Modules.ACL.User;
import com.BamoOS.Modules.Communication.IPC;
import com.BamoOS.Modules.FileSystem.Catalog;
import com.BamoOS.Modules.FileSystem.File;
import com.BamoOS.Modules.FileSystem.FileBase;
import com.BamoOS.Modules.FileSystem.IFileSystem;
import com.BamoOS.Modules.MemoryManagment.RAM;
import com.BamoOS.Modules.ProcessManager.IProcessManager;
import com.BamoOS.Modules.ProcessManager.PCB;
import com.BamoOS.Modules.ProcessManager.ProcessManager;
import com.BamoOS.Modules.Processor.IProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static com.BamoOS.Modules.ACL.OperationType.MODIFY;
import static com.BamoOS.Modules.ACL.OperationType.READ;


public class Shell {
    private IProcessor processor;
    private ProcessManager processManager;
    private RAM memory;
    private IFileSystem fileSystem;
    private ILoginService loginService;
    private IUserController userController;
    private IACLController ACLController;
    private IPC ipc;
    private Map<String, String> allCommands; //Mapa z wszystkimi komednami w shellu

    public Shell(IUserController userController,
                 IFileSystem fileSystem, RAM memory,
                 IProcessor processor,
                 IACLController ACLController,
                 ProcessManager processManager,
                 ILoginService loginService, IPC ipc) {
        this.userController = userController;
        this.fileSystem = fileSystem;
        this.memory = memory;
        this.processor = processor;
        this.ACLController = ACLController;
        this.processManager = processManager;
        this.loginService = loginService;
        this.ipc=ipc;

        allCommands = new HashMap<>();
    }
    /**
     * Metoda, ktora wypelnia mape komendami, wyswietla logo, oraz dopoki nie zostanie przerwana przez uzytkownika wykonuje metode readCommend()
     */
    public void start() {
        addAllCommands();
        logo();
        loginLoad(); // zalogowanie sie uzytkownika
        while (true) {
            readCommend();
        }
    }
    /**
     * Metoda, ktora laduje wszytskie metody do mapy
     */
    private void addAllCommands() {
        allCommands.put("man", "Pomoc");
        allCommands.put("exit", "Koniec pracy systemu");
        allCommands.put("uname", "Informacje o autorach systemu");
        allCommands.put("user", "Kontrola uzytkownikow");
        allCommands.put("users", "Wyświetla listę użytkowników ");
        allCommands.put("groups", "Wyświetla listę grup");
        allCommands.put("group", "Kontrola grup");
        allCommands.put("cr", "Tworzenie wpisu");
        allCommands.put("cat", "wyswietlenie pliku/dodanie na koniec pliku");
        allCommands.put("ls", "Wyswietla zawartosc katalogow");
        allCommands.put("process", "Dzaialnia dotyczace procesu");
        allCommands.put("pcbinfo", "Blok kontrolny");
        allCommands.put("go", "Wykonanie jednego rozkau");
        allCommands.put("login", "Zamiana uzytkownika");
        allCommands.put("open", "Otwieranie pliku");
        allCommands.put("close", "Zamykanie pliku");
        allCommands.put("access", "Dodanie uprawnień do pliku dla konkretnego  użytkownika ");
        allCommands.put("whoami", "Wyswietla aktualnie zalogowanego uzytkownika ");
        allCommands.put("meminfo", "Wyswietlenie RAM");
        allCommands.put("cv", "Wyswietlenie informacji o zmiennej warunkowej");
        allCommands.put("sms", "Wyswietlenie wszytskich komunikatow wyslanych podczas komunikacji miedzyprocesorowej");
    }
    private void logo() {

        System.out.println( "\n" +
                "__/\\\\\\\\\\_____/\\\\\\_        _____/\\\\\\\\\\\\\\\\\\____        ________/\\\\\\\\\\\\\\\\\\_        __/\\\\\\________/\\\\\\_        _______/\\\\\\\\\\______        _____/\\\\\\\\\\\\\\\\\\\\\\___        \n" +
                " _\\/\\\\\\\\\\\\___\\/\\\\\\_        ___/\\\\\\\\\\\\\\\\\\\\\\\\\\__        _____/\\\\\\////////__        _\\/\\\\\\_______\\/\\\\\\_        _____/\\\\\\///\\\\\\____        ___/\\\\\\/////////\\\\\\_       \n" +
                "  _\\/\\\\\\/\\\\\\__\\/\\\\\\_        __/\\\\\\/////////\\\\\\_        ___/\\\\\\/___________        _\\/\\\\\\_______\\/\\\\\\_        ___/\\\\\\/__\\///\\\\\\__        __\\//\\\\\\______\\///__      \n" +
                "   _\\/\\\\\\//\\\\\\_\\/\\\\\\_        _\\/\\\\\\_______\\/\\\\\\_        __/\\\\\\_____________        _\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\_        __/\\\\\\______\\//\\\\\\_        ___\\////\\\\\\_________     \n" +
                "    _\\/\\\\\\\\//\\\\\\\\/\\\\\\_        _\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\_        _\\/\\\\\\_____________        _\\/\\\\\\/////////\\\\\\_        _\\/\\\\\\_______\\/\\\\\\_        ______\\////\\\\\\______    \n" +
                "     _\\/\\\\\\_\\//\\\\\\/\\\\\\_        _\\/\\\\\\/////////\\\\\\_        _\\//\\\\\\____________        _\\/\\\\\\_______\\/\\\\\\_        _\\//\\\\\\______/\\\\\\__        _________\\////\\\\\\___   \n" +
                "      _\\/\\\\\\__\\//\\\\\\\\\\\\_        _\\/\\\\\\_______\\/\\\\\\_        __\\///\\\\\\__________        _\\/\\\\\\_______\\/\\\\\\_        __\\///\\\\\\__/\\\\\\____        __/\\\\\\______\\//\\\\\\__  \n" +
                "       _\\/\\\\\\___\\//\\\\\\\\\\_        _\\/\\\\\\_______\\/\\\\\\_        ____\\////\\\\\\\\\\\\\\\\\\_        _\\/\\\\\\_______\\/\\\\\\_        ____\\///\\\\\\\\\\/_____        _\\///\\\\\\\\\\\\\\\\\\\\\\/___ \n" +
                "        _\\///_____\\/////__        _\\///________\\///__        _______\\/////////__        _\\///________\\///__        ______\\/////_______        ___\\///////////_____\n" +
                "                                                                                                                                                                  \n" +
                "                                                                                                                                              \n");
    }
    /**
     * Metoda ktora wywoluje sie dopoki uzytkownik nie poda prawidlowego loginu lub poda exit, wtedy kończy się praca systemu
     */
    private void loginLoad() {
        System.out.println("Podaj login:");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line = in.readLine();
            if (line.equals("exit")) {
                exit();
            } else {
                login(line); // prywatna metoda, ktora ma w sobie loginService
            }
        } catch (IOException e) {
            e.getStackTrace();
            readCommend();
        }
    }
    /**
     * Metoda, ktora zostaje wywolana na poczatku uruchamiania sie shella, loguje użytkownika do systemu
     * @param name
     */
    private void login(String name) {
        try {
            loginService.loginUser(name);
        } catch (Exception e) {
            System.out.println("Nie ma takiego uzytkownika");
            loginLoad();
        }
    }
    /**
     * Metoda, ktora czyta komende od uzytkownika z konsoli, pobiera stringa, ktory dzieli na czesci przedzielone spacja
     * i w zaleznosci od piewsego czlonu komendy wywoluje inne prywante metody shella
     */
    private void readCommend() {
        System.out.print(loginService.getLoggedUser().getName() + ">"); // podaje nazwe zalogowanego uzytkownika podczas kazdej komendy
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line = in.readLine();
            if (line.length() > 0) {
                String[] separateCommand = line.split(" "); // podzielenie komendy na czesci odzielone spacja
                if (separateCommand.length > 0) {
                    if (isCommandGood(separateCommand[0])) {
                        switch (separateCommand[0]) {
                            case "man":
                                man();
                                break;
                            case "exit":
                                exit();
                                break;
                            case "uname":
                                uname();
                                break;
                            case "user":
                                user(separateCommand);
                                break;
                            case "login":
                                changeLogin(separateCommand);
                                break;
                            case "whoami":
                                whoLogin(separateCommand);
                                break;
                            case "users":
                                users(separateCommand);
                                break;
                            case "group":
                                group(separateCommand);
                                break;
                            case "cr":
                                create(separateCommand);
                                break;
                            case "cat":
                                cat(separateCommand);
                                break;
                            case "access":
                                access(separateCommand);
                                break;
                            case "ls":
                                ls(separateCommand);
                                break;
                            case "process":
                                process(separateCommand);
                                break;
                            case "pcbinfo":
                                pcbinfo(separateCommand);
                                break;
                            case "meminfo":
                                meminfo(separateCommand);
                                break;
                            case "go":
                                go(separateCommand);
                                break;
                            case "open":
                                open(separateCommand);
                                break;
                            case "close":
                                close(separateCommand);
                                break;
                            case "cv":
                                conditionVariable(separateCommand);
                                break;
                            case "sms":
                                sms(separateCommand);
                                break;
                            case "groups":
                                groups(separateCommand);
                                break;
                        }
                    } else if (!isCommandGood(separateCommand[0])) {
                        System.out.println("Bledna komenda");
                        man();
                    }
                }
            } else {
                readCommend();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            readCommend();
        }
    }
    /**
     * Metoda, ktora sprawdza, czy komenda podana przez uzytkownika, znajduje sie wsrod komend zapisanych w mapie shella
     * @param command
     * @return zwraca ture, gdy jest, false gdy nie ma
     */
    private boolean isCommandGood(String command) {
        if (allCommands.containsKey(command)) return true;
        return false;
    }
    /**
     * Metoda, ktora zostaje wywolana, gdy uzytkownik poda komende 'man' : Wyswietla ona wszystkie komendy dostepne
     * dla uzytkownika i ich opis
     */
    private void man() {
        //man
        System.out.println("            POMOC");
        System.out.println("Komenda:          Opis:");
        for (Map.Entry<String, String> command : allCommands.entrySet()) {
            if (command.getKey().length() == 2) {
                System.out.println(command.getKey() + "                " + command.getValue());
            } else if (command.getKey().length() == 4) {
                System.out.println(command.getKey() + "              " + command.getValue());
            } else if (command.getKey().length() == 7) {
                System.out.println(command.getKey() + "           " + command.getValue());
            } else if (command.getKey().length() == 3) {
                System.out.println(command.getKey() + "               " + command.getValue());
            } else if (command.getKey().length() == 5) {
                System.out.println(command.getKey() + "             " + command.getValue());
            } else {
                System.out.println(command.getKey() + "            " + command.getValue());
            }
        }
    }
    /**
     * Metoda, ktora zostaje wywolana, gdy uzytkownik poda komende 'exit'
     * Asekuracyjnie sprawdza czy uzytkownik chce zakonczyc prace,
     * jesli tak-> koniec pracy systemu
     * jesli nie-> kontynuuje odczytywanie komendy
     */
    private void exit() {
        //exit
        System.out.println("Chcesz zakonczyc prace systemu ?[ TAK - T, NIE - N]");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            String answer = in.readLine();
            if (answer.equals("t") | answer.equals("T")) {
                System.exit(0);
            } else if (answer.equals("n") | answer.equals("N")) {
                readCommend();
            } else {
                System.out.println("Bledna odpowiedz");
                exit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Metoda, ktora zostaje wywolana, gdy uzytkownik poda komedne 'uname'
     * Wyswietla ona informacje dotyczace autorow systemu operacyjnego
     */
    private void uname() {
        //uname
        System.out.println("Autorzy systemu: ");
        System.out.println("Klaudia Bartoszak        Zarzadzanie pamiecia");
        System.out.println("Kamila Urbaniak          Interpreter/Programy ");
        System.out.println("Agnieszka Rusin          Shell");
        System.out.println("Marcin Hilt              Zarzadzanie procesorem ");
        System.out.println("Jakub Smierzchalski      Komunikacja miedzyprocesorowa");
        System.out.println("Michał Sciborski         Konta/Grupy");
        System.out.println("Bartosz Wieckowski       Zarzadzanie procesami");
        System.out.println("Michal Wlodarczyk        Zarzadrzanie pliaki/katalogami");
        System.out.println("Jedrzej Wyzgala          Mechanizm synchronizacji");
    }
    /**
     * Metoda, ktora zostaje wywolana, gdy uzytkownik poda komedne 'user ...' z pewnymi parametrami
     * Wywolywane sa tutaj metody userControllera
     * @param command
     */
    private void user(String[] command) {
        if (command.length > 1) {
            if (command[1].equals("--add")) {
                //user --add [nazwa_uzytkownika] --group [nazwa_groupy]
                if (command.length == 5) {
                    try {
                        userController.addUser(command[2], command[4]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }
                //user --add [nazwa_uzytkownika]
                else if (command.length == 3) {
                    try {
                        userController.addUser(command[2], null);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                } else {
                    System.out.println("Bledna komenda");
                    readCommend();
                }
            } else if (command[1].equals("--remove")) {
                //user --remove [nazwa_uzytkownika]
                if (command.length == 3) {
                    try {
                        userController.removeUser(command[2]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                } else {
                    System.out.println("Bledna komenda");
                    readCommend();
                }
            }else if(command[1].equals("--showgroups")){
                //user --showgroups
                if(command.length==2){
                    try {
                       System.out.println( userController.printUserGroups(loginService.getLoggedUser().getName()));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }else{
                    System.out.println("Bledna komenda");
                    readCommend();
                }
            }
            else {
                System.out.println("Bledna komenda");
                readCommend();
            }
        }
    }
    /**
     * Metoda ktora zostaje wywolana gdy uzytkowniki poda komende users, wyswietla ona liste uzytkownikow
     * Wywolywane sa tutaj metody userControllera
     * @param command
     */
    private void users(String[] command) {
        //users
        if (command.length == 1) {
            System.out.println(userController.showUserList());
        } else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda, ktora zostaje wywolana, gdy uzytkownik poda komedne 'group. ...'
     * Wywolywane sa tutaj metody userControllera
     * @param command
     */
    private void group(String[] command) {
        if (command.length > 1) {
            // group --add [nazwa_uzytkownika] [nazwa_grupy]
            if (command[1].equals("--add")) {
                if (command.length == 4) {
                    try {
                        userController.addUserToGroup(userController.getUser(command[2]), command[3]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                } else {
                    System.out.println("Bledna komenda");
                    readCommend();
                }

            } else if (command[1].equals("--remove")) {
                //group --remove [nazwa_uzytkownika]
                if (command.length == 3) {
                    try {
                        userController.removeUserFromAllGroups(userController.getUser(command[2]));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }
                //group --remove [nazwa_uzytkownika] [nazwa_grupy]
                if (command.length == 4) {
                    try {
                        userController.removeUserFromGroup(command[2], command[3]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                } else {
                    System.out.println("Bledna komenda");
                    readCommend();
                }
            }
        } else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }

    /**
     * Metoda, ktora zostaje wywolalan gdy uzytkownik poda komende 'groups'
     * Wyswetla liste grup.
     * @param command
     */
    private void groups(String[] command){
        //groups
        if (command.length == 1) {
            System.out.println(userController.printGroups());
        } else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda, ktora zostaje wywolalan gdy uzytkownik poda komende 'cr ...'
     * Wywoływane sa tutaj metody ACLControllera oraz filesystem oraz loginService
     * @param command
     */
    private void create(String[] command) {
        if (command.length > 1) {
            //cr [nazwa_pliku]
            if (command.length == 2) {
                if (ACLController.hasUserPremissionToOperation(fileSystem.getCatalog(), loginService.getLoggedUser(), MODIFY)) { //sprawdzenie uprawnien
                    //tworzenie pliku
                    try {
                        fileSystem.createFile(command[1], loginService.getLoggedUser(), processManager);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                    FileBase fileBase = null;
                    try {
                        fileBase = fileSystem.getFileBase(command[1]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                    try {
                        ACLController.setDefaultPremissionToFile(fileBase); //nadanie uprawnieni do utworzonego pliku
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                } else {
                    System.out.println("Brak uprawnien do katalogu");
                    readCommend();
                }
            } else {
                System.out.println("Bledna komenda");
                readCommend();
            }
        } else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda, ktora zostanie wywolana gdy uzytkwonik poda komende 'cat ...'
     * Wywoływane sa tutaj metody ACLControllera oraz filesystem oraz loginService
     * Metoda readFile zwraca w Stringu zawartosc pliku zczytana z dysku
     * @param command
     */
    private void cat(String[] command) {
        //cat > [nazwa_pliku]
        // {zawartosc do dodania} // zakonczenie wpisywania danych -> "^D"
        if (command.length == 3) {
            if (command[1].equals(">")) {
                FileBase fileBase = null;
                try {
                    fileBase = fileSystem.getFileBase(command[2]);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }
                if (ACLController.hasUserPremissionToOperation(fileBase, loginService.getLoggedUser(), MODIFY)) {  //sprawdzenie uprawnien
                    try {
                        System.out.println("Podaj zawartosc do pliku : ");
                        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                        StringBuilder out = new StringBuilder();
                        Pattern pattern;
                        Matcher matcher;
                        String end = "(.)*\\^D";
                        while (true) {
                            String line = in.readLine();
                            pattern = Pattern.compile(end);
                            matcher = pattern.matcher(line);
                            if (!matcher.lookingAt()) { // jesli nie zawiera linijka ^D to :
                                out.append(line);
                                out.append('\n');
                            } else {
                                //dodanie bez ^D // jesli dana linijka zawiera ^D
                                int size = line.length();
                                StringBuilder str = new StringBuilder(line);
                                str.deleteCharAt(size - 2); //usuniecie D
                                str.deleteCharAt(size - 2); // usuniecie ^
                                out.append(str.toString());
                                break;
                            }
                        }
                        String content = new String(out.toString());
                        try {
                            fileSystem.appendFile(command[2], content);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            readCommend();
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                } else {
                    System.out.println("Brak uprawnien do pliku");
                    readCommend();
                }
            } else {
                System.out.println("Bledne parametry");
                readCommend();
            }
        }
        //cat [nazwa_pliku] //wyswietla zawartosc pliku
        else if (command.length == 2) {
            FileBase fileBase = null;
            try {
                fileBase = fileSystem.getFileBase(command[1]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                readCommend();
            }
            if (ACLController.hasUserPremissionToOperation(fileBase, loginService.getLoggedUser(), READ)) {  //sprawdzenie uprawnien
                try {
                    System.out.println(fileSystem.readFile(command[1]));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }
            } else {
                System.out.println("Brak uprawnien do pliku");
                readCommend();
            }
        } else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda, ktora zostaje wywoalana gdy uzytkownik poda komende 'ls'
     * Wywoływane sa tutaj metody ACLControllera oraz filesystem oraz loginService
     * Wyświetla  nazwy i rozmiar wpisów w katalogu domyślnym
     * @param command
     */
    private void ls(String[] command) {
        //ls
        if (command.length == 1) {
            Catalog catalog = null;
            try {
                catalog = fileSystem.getCatalog();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                readCommend();
            }
            if (ACLController.hasUserPremissionToOperation(catalog, loginService.getLoggedUser(), READ)) {
                try {
                    System.out.println("Nazwa pliku|rozmiar|pierwszyBlok|OstatniBlok");
                    System.out.println(fileSystem.list());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }

            } else {
                System.out.println("Brak uprawnien do katalogu");
                readCommend();
            }
        } else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     *Metoda, ktora zostaje wywolanna gdy uzytkownik poda komende 'access ...'
     * Wywoływane sa tutaj metody ACLControllera oraz filesystem oraz loginService
     * Dodanie uprawnień do pliku dla konkretnego  użytkownika
     * Jeżeli jest właścicielem pliku, lub adminem może nadawać uprawnienia
     */
    private void access(String[] command) {
        if (command.length == 5) {
            //access [file_name] --user [user_name] (R)(M)(E) 111/000 ...
            if (command[2].equals("--user")) {
                User user = null;
                try {
                    user = userController.getUser(command[3]);
                } catch (Exception e) {
                    System.out.println( e.getMessage());
                    readCommend();
                }
                if(isAdmin(user)){
                    FileBase fileBase = null;
                    try {
                        fileBase = fileSystem.getFileBase(command[1]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                    try {
                        ACLController.addAceForUser(user, whichMask(command[4]), fileBase);
                    } catch (Exception e) {
                        System.out.println( e.getMessage());
                        readCommend();
                    }
                }else try {
                    if(fileSystem.getFileBase(command[1]).getOwner().getName().equals(loginService.getLoggedUser().getName())){
                        FileBase fileBase = null;
                        try {
                            fileBase = fileSystem.getFileBase(command[1]);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            readCommend();
                        }
                        try {
                            ACLController.addAceForUser(user, whichMask(command[4]),fileBase);
                        } catch (Exception e) {
                           System.out.println( e.getMessage());
                            readCommend();
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }
            }
            //access [file_name] --group [group_name] (R)(M)(E) 111/000 ...
           else if (command[2].equals("--group")) {
                Group group = null;
                try {
                    group = userController.getGroup(command[3]);
                } catch (Exception e) {
                    System.out.println( e.getMessage());
                    readCommend();
                }
                FileBase fileBase = null;
                try {
                    fileBase = fileSystem.getFileBase(command[1]);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }
                try {
                    ACLController.addAceForGroup(group, whichMask(command[4]), fileBase);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }
            }else {
                System.out.println("Bledna komenda");
                readCommend();
            }
        } else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metda, ktora zostanie wywolana gdy uzytkownik poda komende ' process [...] '
     * Wywoływane metodu procesManager
     * @param command
     */
    private void process(String[] command) {
        if (command.length > 1) {
            if (command.length == 3) {
                //process --kill [PID]
                if (command[1].equals("--kill")) {
                    //zakonczenie pracy procesu
                    try {
                        processManager.killProcess(Integer.parseInt(command[2]));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }
                //process --killall [PGID]
                else if (command[1].equals("--killall")) {
                    try {
                        processManager.killProcessGroup(Integer.parseInt(command[2]));
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }
                //process -–create [nazwaProcesu] [nazwaPliku][PGID]
            }
            else if (command.length == 5) {
                    if (command[1].equals("--create")) {
                        try {
                            processManager.newProcess(command[2], Integer.parseInt(command[4]), command[3]);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            readCommend();
                        }
                    }else {
                        System.out.println("Bledna komenda");
                        readCommend();
                    }
            }
            //process –-groupCreate [nazwaProcesu] [nazwaPliku]  // nowa grupa
            else if (command.length==4){
                if (command[1].equals("--groupCreate")) {
                    try {
                        processManager.newProcessGroup(command[2], command[3]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }else{
                    System.out.println("Bledna komenda");
                    readCommend();
                }
            } else {
                System.out.println("Bledna komenda");
                readCommend();
            }
        }
    }
    /**
     * Metoda, ktora zostanie wywolana gdy użytkownik poda komende 'pcbinfo'
     * Wyswietla blok kontrolny
     * Metody  PCB
     * @param command
     */
    private void pcbinfo(String[] command) {
        //pcbinfo --active
        if (command.length == 2) {
            if (command[1].equals("--active")) {
                // wyswietlanie bloku kontrolengo aktywnego procesu
                try {
                    processManager.getActivePCB().printInfo();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }

            }//pcbinfo --all
            else if (command[1].equals("--all")) {
                try {
                    processManager.PrintProcesses();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }
            } else {
                System.out.println("Bledna komenda");
                readCommend();
            }
        }
        //pcbinfo --all [PGID]
        else if(command.length==3){
            if(command[1].equals("--all")) {
                try {
                    processManager.PrintGroupInfo(Integer.parseInt(command[2]));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }
            }
            //pcbinfo --process [PID]
            else if(command[1].equals("--process")){
                PCB pcb=null;
                try {
                    pcb = processManager.getPCB(Integer.parseInt(command[2]));
                    pcb.printInfo();
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }

            }else{
                System.out.println("Bledna komenda");
                readCommend();
            }
        }
        else {
            System.out.println("Bledna komenda");
            readCommend();
            }
    }
    /**
     * Metdoa, ktora zostanie wywwoalan gdy uzytwkonik poda komende 'meminfo'
     * Metody modułu memory
     * @param command
     */
    private void meminfo(String[] command){
        // meminfo --print
        if(command.length==2){
            if(command[1].equals("--print")){
               memory.writeRAM();
            }
            else {
                System.out.println("Bledna komenda");
                readCommend();
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda która zostanie wywyołana gdy użytkownik poda komende 'go'
     * Wykonuje jedna linijkę jakiegoś procesu
     * Metoda modułu porcesora
     * @param command
     */
    private void go(String[]command){
        if(command.length==1){
            try {
                processor.exe();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                readCommend();
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda, ktora zostaje wywolanna gdy uzytkownik poda komende 'login '
     * Zmienia zalogowanego uzytkownika
     * @param command
     */
    private void changeLogin(String[] command){
        //login [nazwaUsera]
        if(command.length==2) {
            try {
                loginService.loginUser(command[1]);
            } catch (Exception e) {
                System.out.println("Nie ma takiego uzytkownika");
                readCommend();
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda, ktora zostaje wywolanna gdy uzytkownik poda komende 'whoami'
     * wyswietla aktualnie zalogowanego uzytkownika
     * @param command
     */
    private void whoLogin(String[] command){
        if(command.length==1) {
            System.out.println(loginService.getLoggedUser().getName());
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     *Pomocnicza metoda, do sprawdzenia maski podanej w komendzie przez użytkownika
     * @param m // maska
     * @return Mask - potrzebną do nadania uprawnień do pliku
     */
    private Mask whichMask(String m){
        Mask mask= new Mask (false, false, false);
        switch (m){
            case "000":
                mask=new Mask(false,false,false);
                break;
            case "001":
                mask= new Mask(false, false, true);
                break;
            case "010":
                mask= new Mask(false, true, false);
                break;
            case "011":
                mask= new Mask(false, true, true);
                break;
            case "100":
                mask= new Mask( true, false, false);
                break;
            case "101":
                mask= new Mask( true, false, true);
                break;
            case "110":
                mask= new Mask( true, true, false);
                break;
            case "111":
                mask= new Mask( true, true, true);
                break;
            default:
                break;
        }
    return mask;

    }
    /**
     *Metoda, ktora zostaje wywolanna gdy uzytkownik poda komende 'open ... '
     * otwiera plik
     * @param command
     */
    private void open(String[] command){
        //open [nazwa_pliku]
        if(command.length==2){
            try {
                fileSystem.openFile(command[1]);
            }catch(Exception e) {
                System.out.println(e.getMessage());
                readCommend();
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     *Metoda, ktora zostaje wywolanna gdy uzytkownik poda komende 'close ..'
     * zamuka plik
     * @param command
     */
    private void close(String[] command){
        //close [nazwa_pliku]
        if(command.length==2){
            try {
                fileSystem.closeFile(command[1]);
            }catch(Exception e) {
                System.out.println(e.getMessage());
                readCommend();
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda, która sprawdza czy użytwkonik zalogoany jest adminem
     * @param user
     * @return true jest tak
     */
    private boolean isAdmin(User user){
        try {
            for(Group group : userController.getUserGroups(user.getName())){
                if(group.getName().equals("admin")){
                    return true;
                }
            }
        } catch (Exception e) {
           System.out.println( e.getMessage());
            readCommend();
        }
        return false;
    }
    /**
     * Metoda, ktora zostaje wywolana gdy uzytkownik poda komende 'cv'
     * @param command
     */
    private void conditionVariable(String[] command){
        //cv --file [nazwaPliku]
        if(command.length==3){
            if(command[2].equals("--file")) {
                File file = null;
                try {
                    file = fileSystem.getFile(command[1]);
                    file.cv.printInfo();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    readCommend();
                }
            }
        }
        else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda, ktora zostanie wywolana gdy uzytkownik poda komede 'sms'
     * @param command
     */
    private void sms(String[] command){
        //sms
        if(command.length==1) {
            ipc.display_all();
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }

}
