import com.BamoOS.Modules.ACL.ACLController;
import com.BamoOS.Modules.ACL.Interfaces.IACLController;
import com.BamoOS.Modules.ACL.Interfaces.ILoginService;
import com.BamoOS.Modules.ACL.Interfaces.IUserController;
import com.BamoOS.Modules.ACL.LoginService;
import com.BamoOS.Modules.ACL.UserController;

public class Main {



        public static void main(String[] args) {


           ProcesorInterface procesor = new ProcesorInterface();
           IProcessManager processManager = new IProcessManager();
           RAM memory = new RAM();
           IFileSystem fileSystem = new IFileSystem();
           IUserController userController= new UserController();
           ILoginService loginService= new LoginService(userController);
           IACLController ACLController = new ACLController(userController);
           IPCB PCB= new PCB();



            com.BambOS.com.BambOS.Modules.Shell shell = new com.BambOS.com.BambOS.Modules.Shell( userController,  fileSystem,  memory,  procesor, ACLController,  processManager, PCB, loginService );
            shell.start();





}
