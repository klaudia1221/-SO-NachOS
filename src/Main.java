import com.BamoOS.Modules.ACL.ACLController;
import com.BamoOS.Modules.ACL.Interfaces.IACLController;
import com.BamoOS.Modules.ACL.Interfaces.ILoginService;
import com.BamoOS.Modules.ACL.Interfaces.IUserController;
import com.BamoOS.Modules.ACL.LoginService;
import com.BamoOS.Modules.ACL.UserController;
import com.BamoOS.Modules.Shell.*;

public class Main{
    private static ILoginService loginService;
    private static IUserController userController;
    private static IACLController aclController;
        public static void main(String[] args) {
            userController = new UserController();
            loginService = new LoginService(userController);
            aclController = new ACLController(userController);

            ProcesorInterface procesor = new ProcesorInterface();
            IProcessManager processManager = new IProcessManager();
            RAM memory = new RAM();
            IFileSystem fileSystem = new IFileSystem();
            ILoginService loginService = new LoginService();
            IUserControl
            ler userController = new UserController();
            IPCB PCB = new PCB();
            IACLController ACLController = new IACLController();


            Shell shell = new Shell(userController, fileSystem, memory, procesor, ACLController, processManager, PCB, loginService);
            shell.start();


        }

}
