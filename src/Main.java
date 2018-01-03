import com.BamoOS.Modules.ACL.ACLController;
import com.BamoOS.Modules.ACL.Interfaces.IACLController;
import com.BamoOS.Modules.ACL.Interfaces.ILoginService;
import com.BamoOS.Modules.ACL.Interfaces.IUserController;
import com.BamoOS.Modules.ACL.LoginService;
import com.BamoOS.Modules.ACL.UserController;
import com.BamoOS.Modules.ConditionVariable.ConditionVariable;
import com.BamoOS.Modules.ConditionVariable.IConditionVariable;
import com.BamoOS.Modules.FileSystem.Catalog;
import com.BamoOS.Modules.FileSystem.FileSystem;
import com.BamoOS.Modules.FileSystem.IFileSystem;
import com.BamoOS.Modules.MemoryManagment.RAM;
import com.BamoOS.Modules.ProcessManager.IProcessManager;
import com.BamoOS.Modules.ProcessManager.PCB;
import com.BamoOS.Modules.ProcessManager.ProcessManager;
import com.BamoOS.Modules.Shell.*;


public class Main{

    private static ILoginService loginService;
    private static IUserController userController;
    private static IACLController aclController;

    private static IFileSystem fileSystem;
    private static Catalog catalog;

    private static IConditionVariable conditionVariable;
    private static ProcessManager processManager;
        public static void main(String[] args) {
            processManager = new ProcessManager();

            userController = new UserController();
            loginService = new LoginService(userController);
            aclController = new ACLController(userController);
            CreateDefaultUsers();

            CreateCatalog();
            fileSystem = new FileSystem(catalog);;

            conditionVariable = new ConditionVariable(processManager);


            ProcesorInterface processor = new ProcesorInterface();
            RAM memory = new RAM();
            PCB PCB = new PCB();


            Shell shell = new Shell( userController,  fileSystem,  memory,  processor, aclController,  processManager, loginService, conditionVariable );
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
