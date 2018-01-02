import com.BamoOS.Modules.ACL.ACLController;
import com.BamoOS.Modules.ACL.Interfaces.IACLController;
import com.BamoOS.Modules.ACL.Interfaces.ILoginService;
import com.BamoOS.Modules.ACL.Interfaces.IUserController;
import com.BamoOS.Modules.ACL.LoginService;
import com.BamoOS.Modules.ACL.UserController;
import com.BamoOS.Modules.FileSystem.Catalog;
import com.BamoOS.Modules.FileSystem.FileSystem;
import com.BamoOS.Modules.FileSystem.IFileSystem;
import com.BamoOS.Modules.Shell.*;

public class Main{
    private static ILoginService loginService;
    private static IUserController userController;
    private static IACLController aclController;
    private static IFileSystem fileSystem;
    private static Catalog catalog;
        public static void main(String[] args) {
            userController = new UserController();
            loginService = new LoginService(userController);
            aclController = new ACLController(userController);
            CreateDefaultUsers();
            CreateCatalog();
            fileSystem = new FileSystem(catalog);;

            ProcesorInterface procesor = new ProcesorInterface();
            IProcessManager processManager = new IProcessManager();
            RAM memory = new RAM();
            IPCB PCB = new PCB();


            com.BambOS.com.BambOS.Modules.Shell shell = new Shell(userController, fileSystem, memory, procesor, ACLController, processManager, PCB, loginService);
            shell.start();
        }
        private static void CreateDefaultUsers(){
            try {
                userController.addUser("Admin", "admin");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                userController.addUser("DefaultUser", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                userController.addUser("TestUser","test");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private static void CreateCatalog(){
            try {
                catalog = new Catalog(userController.getUser("Admin"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

}
