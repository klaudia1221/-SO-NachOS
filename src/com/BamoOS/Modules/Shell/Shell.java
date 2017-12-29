package com.BambOS.com.BambOS.Modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static com.BambOS.com.BambOS.Modules.OperationType.MODIFY;
import static com.BambOS.com.BambOS.Modules.OperationType.READ;


public class Shell {
    private String testString;
    private ProcesorInterface procesor;
    private IProcessManager processManager;
    private RAM memory;
    private IFileSystem fileSystem;
    private ILoginService loginService;
    private IUserController userController;
    private IPCB PCB;
    private IACLController ACLController;
    private Map<String, String> allCommands; //Mapa z wszystkimi komednami w shellu

    public Shell(IUserController userController,  IFileSystem fileSystem, RAM memory, ProcesorInterface procesor, IACLController ACLController, IProcessManager processManager, IPCB PCB, ILoginService loginService ) {
        this.userController = userController;
        this.fileSystem = fileSystem;
        this.memory = memory;
        this.procesor = procesor;
        this.ACLController= ACLController;
        this.processManager = processManager;
        this.PCB= PCB;
        this.loginService = loginService;
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
        allCommands.put("group", "Kontrola grup");
        allCommands.put("cr", "Tworzenie wpisu");
        allCommands.put("cat", "wyswietlenie pliku/dodanie na koniec pliku");
        allCommands.put("rm", "Usuwa plik o podanej nazwie");
        allCommands.put("mv", "Zmiana nazwy pliku");
        allCommands.put("ls", "Wyswietla zawartosc katalogow");
        allCommands.put("process", "Dzaialnia dotyczace procesu");
        allCommands.put("memory", "Wyswietlenie pamieci");
        allCommands.put("pcbinfo", "Blok kontrolny");
        allCommands.put("go", "Wykonanie jednego rozkau");
        allCommands.put("login", "Zamiana uzytkownika");
        allCommands.put("open", "Otwieranie pliku");
        allCommands.put("close", "Zamykanie pliku");
        allCommands.put("access", "Dodanie uprawnień do pliku dla konkretnego  użytkownika ");
        allCommands.put("whoami", "Wyswietla aktualnie zalogowanego uzytkownika ");
    }

    private void logo(){
        System.out.println("__/\\\\\\\\\\\\\\\\\\\\\\\\\\_______/\\\\\\\\\\\\\\\\\\_____/\\\\\\\\____________/\\\\\\\\__/\\\\\\\\\\\\\\\\\\\\\\\\\\_________/\\\\\\\\\\__________/\\\\\\\\\\\\\\\\\\\\\\___        \n" +
                " _\\/\\\\\\/////////\\\\\\___/\\\\\\\\\\\\\\\\\\\\\\\\\\__\\/\\\\\\\\\\\\________/\\\\\\\\\\\\_\\/\\\\\\/////////\\\\\\_____/\\\\\\///\\\\\\______/\\\\\\/////////\\\\\\_       \n" +
                "  _\\/\\\\\\_______\\/\\\\\\__/\\\\\\/////////\\\\\\_\\/\\\\\\//\\\\\\____/\\\\\\//\\\\\\_\\/\\\\\\_______\\/\\\\\\___/\\\\\\/__\\///\\\\\\___\\//\\\\\\______\\///__      \n" +
                "   _\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\__\\/\\\\\\_______\\/\\\\\\_\\/\\\\\\\\///\\\\\\/\\\\\\/_\\/\\\\\\_\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\___/\\\\\\______\\//\\\\\\___\\////\\\\\\_________     \n" +
                "    _\\/\\\\\\/////////\\\\\\_\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\_\\/\\\\\\__\\///\\\\\\/___\\/\\\\\\_\\/\\\\\\/////////\\\\\\_\\/\\\\\\_______\\/\\\\\\______\\////\\\\\\______    \n" +
                "     _\\/\\\\\\_______\\/\\\\\\_\\/\\\\\\/////////\\\\\\_\\/\\\\\\____\\///_____\\/\\\\\\_\\/\\\\\\_______\\/\\\\\\_\\//\\\\\\______/\\\\\\__________\\////\\\\\\___   \n" +
                "      _\\/\\\\\\_______\\/\\\\\\_\\/\\\\\\_______\\/\\\\\\_\\/\\\\\\_____________\\/\\\\\\_\\/\\\\\\_______\\/\\\\\\__\\///\\\\\\__/\\\\\\_____/\\\\\\______\\//\\\\\\__  \n" +
                "       _\\/\\\\\\\\\\\\\\\\\\\\\\\\\\/__\\/\\\\\\_______\\/\\\\\\_\\/\\\\\\_____________\\/\\\\\\_\\/\\\\\\\\\\\\\\\\\\\\\\\\\\/_____\\///\\\\\\\\\\/_____\\///\\\\\\\\\\\\\\\\\\\\\\/___ \n" +
                "        _\\/////////////____\\///________\\///__\\///______________\\///__\\/////////////_________\\/////_________\\///////////_____\n" +
                "\n" +
                "                                   ~Bardzo Amatorski, Modulowy, ale Bezpieczny Operacyjny System~");
    }
    /**
     * Metoda ktora wywoluje sie dopoki uzytkownik nie poda prawidlowego loginu lub poda exit, wtedy kończy się praca systemu
     */
    private void loginLoad(){
        System.out.println("Podaj login:");
        BufferedReader in= new BufferedReader(new InputStreamReader(System.in));
        try {
            String line=in.readLine();
            if(line.equals("exit")){
                exit();
            }else {
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
    private void login(String name){
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
        System.out.print(loginService.getLoggedUser().getName()+">"); // podaje nazwe zalogowanego uzytkownika podczas kazdej komendy
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
                            case "mv":
                                mv(separateCommand);
                                break;
                            case "rm":
                                rm(separateCommand);
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
            e.printStackTrace();
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
            } else if (command.getKey().length()==4) {
                System.out.println(command.getKey() + "              "+ command.getValue());
            } else if (command.getKey().length()==7) {
                System.out.println(command.getKey() + "           " + command.getValue());
            }
            else if (command.getKey().length()==3) {
                System.out.println(command.getKey() + "               "+ command.getValue());
            }
            else if (command.getKey().length()==5) {
                System.out.println(command.getKey() + "             " + command.getValue());
            }
            else {
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
     * Metoda, ktora zostaje wywolana, gdy uzytkownik poda komedne 'user ...'
     * Wywolywane sa tutaj metody userControllera
     * @param command
     */
    private void user(String[] command) {
        if (command.length > 1) {
            if (command[1].equals("--add")) {
                //user --add [nazwa_uzytkownika] --group [nazwa_groupy]
                if (command.length == 5) {
                    try{
                        userController.addUser(command[2], command[4]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }
                //user --add [nazwa_uzytkownika]
                if (command.length == 3) {
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
            }
            if (command[1].equals("--remove")) {
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
        if (command.length==1) {
            System.out.println(userController.showUserList());
        }
        else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     *Metoda, ktora zostaje wywolana, gdy uzytkownik poda komedne 'group. ...'
     * Wywolywane sa tutaj metody userControllera
     * @param command
     */
    private void group(String[] command){
        if(command.length>1) {
            // group --add [nazwa_uzytkownika] [nazwa_grupy]
            if (command[1].equals("--add")) {
                if(command.length==4) {
                    try {
                        userController.addUserToGroup(userController.getUser(command[2]), command[3]);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }else {
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
                }else {
                    System.out.println("Bledna komenda");
                    readCommend();
                }
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda, ktora zostaje wywolalan gdy uzytkownik poda komende 'cr ...'
     * Wywoływane sa tutaj metody ACLControllera oraz filesystem oraz loginService
     * @param command
     */
    private void create(String[] command){
        if(command.length>1) {
            //cr [nazwa_pliku]
            if (command.length == 3) {
                if (ACLController.hasUserPremissionToOperation(fileSystem.getCatalog(), loginService.getLoggedUser(), MODIFY)) { //sprawdzenie uprawnien
                    try {
                        //tworzenie pliku
                        errorFileSystem(fileSystem.createEmptyFile(command[1])); // errorFileSystem - obsługuje jakiekolwiek errory związane z FileSystem
                        try {
                            ACLController.setDefaultPremissionToFile(fileSystem.getFileBase(command[1])); //nadanie uprawnieni do utworzonego pliku
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            readCommend();
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        readCommend();
                    }
                }
            } else {
                System.out.println("Brak uprawnien do katalogu");
                readCommend();
            }
        }
    }
    /**
     * Metoda, ktora zostanie wywolana gdy uzytkwonik poda komende 'cat ...'
     *  Wywoływane sa tutaj metody ACLControllera oraz filesystem oraz loginService
     *  Metoda readFile zwraca w Stringu zawartosc pliku zczytana z dysku
     * @param command
     */
    private void cat(String[] command) {
        //cat > [nazwa_pliku]
        // {zawartosc do dodania}
        if (command.length == 3) {
            if (command[1].equals(">")) {
                if (ACLController.hasUserPremissionToOperation(fileSystem.getFileBase(command[1]), loginService.getLoggedUser(), MODIFY)) {  //sprawdzenie uprawnien
                        try {
                            System.out.println("Podaj zawartosc do pliku : ");
                            StringBuilder out = new StringBuilder();
                            String content = null;
                            Scanner scanner = new Scanner(System.in);
                            while (scanner.hasNextLine()) {
                                content = new String(scanner.nextLine());
                                out.append(content);
                            }
                            scanner.close();
                            errorFileSystem(fileSystem.appendFile(command[1], content));
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                            readCommend();
                        }
                } else {
                    System.out.println("Brak uprawnien do pliku");
                    readCommend();
                }
            }else {
                System.out.println("Bledne parametry");
                readCommend();
            }
        }
        //cat [nazwa_pliku] //wyswietla zawartosc pliku
        if (command.length == 2) {
            if (ACLController.hasUserPremissionToOperation(fileSystem.getFileBase(command[1]), loginService.getLoggedUser(), READ)) {  //sprawdzenie uprawnien
                if (fileSystem.readFile(command[1]).charAt(0) == '0') {
                    StringBuilder toPrint = new StringBuilder(fileSystem.readFile(command[1]));
                    toPrint.deleteCharAt(0);
                    System.out.println(toPrint.toString());
                } else if (fileSystem.readFile(command[1]).charAt(0) == '2') {  // sprawdza czy plik jest pusty
                    errorFileSystem(2); //bledna nazwa
                } else {
                    errorFileSystem(3); // plik nie otwarty
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
    private void ls(String[] command){
        //ls
        if(command.length==1){
            if (ACLController.hasUserPremissionToOperation(fileSystem.getCatalog(), loginService.getLoggedUser(), READ)) {
                System.out.println(fileSystem.list());
            }else{
                System.out.println("Brak uprawnien do pliku");
                readCommend();
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda, ktora zostaje wywolana gdy uzytkownik poda komende 'mv' [stara_nazwa_pliku] [nowa_nazwa_pliku]'
     * Wywoływane sa tutaj metody ACLControllera oraz filesystem oraz loginService
     * @param command
     */
    private void mv(String[] command) {
        //mv [nazwa_1][nazwa_2]
        if (command.length > 1) {
            if (command.length == 3) {
                if (ACLController.hasUserPremissionToOperation(fileSystem.getFileBase(command[1]), loginService.getLoggedUser(), MODIFY)) {  //sprawdzenie uprawnien
                    errorFileSystem(fileSystem.renameFile(command[1], command[2]));
                }
            }else{
                System.out.println("Brak uprawnien do pliku");
                readCommend();
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda,ktora zostaje wywolalan gdy uzytkownik poda komende 'rm [nazwa_pliku]'
     * usuniecie pliku o podanej nazwie lub zawartosci pliku
     * Wywoływane sa tutaj metody ACLControllera oraz filesystem oraz loginService
     * @param command
     */
    private void rm(String[] command) {
        //rm [nazwa_pliku]
        if (command.length > 1) {
            if (command.length == 2) {
                if (ACLController.hasUserPremissionToOperation(fileSystem.getFileBase(command[1]), loginService.getLoggedUser(), MODIFY)) {  //sprawdzenie uprawnien
                    errorFileSystem(fileSystem.deleteFile(command[1]));
                }else{
                    System.out.println("Brak uprawnien do pliku");
                    readCommend();
                }
            }
            //rm --content [nazwa_pliku]
            if(command.length==3) {
                if (ACLController.hasUserPremissionToOperation(fileSystem.getFileBase(command[1]), loginService.getLoggedUser(), MODIFY)) {  //sprawdzenie uprawnien
                    errorFileSystem(fileSystem.deleteContent(command[1]));
                }else{
                    System.out.println("Brak uprawnien do pliku");
                    readCommend();
                }
            }else
            {
                System.out.println("Bledne parametry");
                readCommend();
            }
        }else {
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     *Metoda, ktora zostaje wywolanna gdy uzytkownik poda komende 'access ...'
     * Wywoływane sa tutaj metody ACLControllera oraz filesystem oraz loginService
     * Dodanie uprawnień do pliku dla konkretnego  użytkownika
     *
     * Jeżeli jest właścicielem pliku, lub adminem może nadawać uprawnienia
     */
    private void access(String[] command) {
        if (command.length == 5) {
            //access [file_name] --user [user_name] (R)(M)(E) 111/000 ...
            if (command[2].equals("--user")) {
                User user = userController.getUser(command[3]);
                if(isAdmin(user)){
                    try {
                        ACLController.addAceForUser(user, whichMask(command[4]), fileSystem.getFileBase(command[1]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(fileSystem.getFileBase(command[1]).getOwner().getName().equals(loginService.getLogged().getName())){
                    try {
                        ACLController.addAceForUser(user, whichMask(command[4]), fileSystem.getFileBase(command[1]));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            //access [file_name] --group [group_name]
            if (command[2].equals("--group")) {
                try {
                    ACLController.addAceForGroup(userController.getUser(command[3]), whichMask(command[4]), fileSystem.getFileBase(command[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
    private void process(String[] command){
        if(command.length>1){
            if(command.length==3){
                //process --kill [PID]
                if(command[1].equals("--kill")){
                    //zakonczenie pracy procesu
                    processManager.KillProcess(Integer.parseInt(command[3]));
                }
                //process --killall [PGID]
                if(command[1].equals("--killall") ){
                    processManager.KillProcessGroup(Integer.parseInt(command[3]));
                }
                //process --ps [PGID]
                if(command[1].equals("--ps")){
                    processManager.PrintGroupInfo(Integer.parseInt(command[2]));
                }
            }
            if(command.length==2){
                //process --ps
                if(command[1].equals("--ps")){
                    processManager.PrintProcesses();
                }
            }
            //process –create [nazwaProcesu] [nazwaPliku]
            if(command.length==4){
                processManager.NewProcessGroup(command[2], command[3]);
            }//process –create [nazwaProcesu] [nazwaPliku] [PGID]
            if(command.length==5) {
                processManager.NewProcessGroup(command[2], command[3], Integer.parseInt(command[4]));
            }
        }else{
            System.out.println("Bledna komenda");
            readCommend();
        }
    }
    /**
     * Metoda, ktora zostanie wywolana gdy użytkownik poda komende 'pcbinfo'
     * Wyswietla blok kontrolny
     * Metody  PCB
     * @param command
     */
    private void pcbinfo(String[] command){
        if(command.length==1){
            // wyswietlanie bloku kontrolengo
            PCB.PrintInfo();
        }else{
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
        if(command.length==2){
            if(command[1].equals("--print")){
                memory.printRAM();
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
            procesor.wykonaj();
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
     *Pomocnicza metoda obslugująca blędy związane z modułem filesystem
     * @param error
     */

    private void errorFileSystem(int error){
        switch (error){
            case 0:
                System.out.println("Operacja wykonana poprawnie");
                break;
            case 1:
                System.out.println("Brak miejsca na dysku");
                break;
            case 2:
                System.out.println("Bledna nazwa");
                break;
            case 3:
                System.out.println("Plik jest nie otwarty");
                break;
            case 4:
                System.out.println("Nowa nazwa istnieje");
                break;
            case 5:
                System.out.println("Plik jest otwraty");
                break;
        }
        readCommend();
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
            errorFileSystem(fileSystem.open(command[1]));
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
            errorFileSystem(fileSystem.open(command[1]));
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
        for(Group group : userController.getUserGroups(user.getName())){
            if(group.getName().equals("admin")){
                return true;
            }
        }
        return false;
    }
}
