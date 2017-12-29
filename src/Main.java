public class Main {



        public static void main(String[] args) {


           ProcesorInterface procesor = new ProcesorInterface();
           IProcessManager processManager = new IProcessManager();
           RAM memory = new RAM();
           IFileSystem fileSystem = new IFileSystem();
           ILoginService loginService= new ILoginService();
           IUserController userController= new IUserController();
           IPCB PCB= new PCB();
           IACLController ACLController = new IACLController();


            Shell shell = new Shell( userController,  fileSystem,  memory,  procesor, ACLController,  processManager, PCB, loginService );
            shell.start();





}
