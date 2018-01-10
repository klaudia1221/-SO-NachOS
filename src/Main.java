import com.NachOS.Modules.ACL.ACLController;
import com.NachOS.Modules.ACL.Interfaces.IACLController;
import com.NachOS.Modules.ACL.Interfaces.ILoginService;
import com.NachOS.Modules.ACL.Interfaces.IUserController;
import com.NachOS.Modules.ACL.LoginService;
import com.NachOS.Modules.ACL.UserController;
import com.NachOS.Modules.Communication.IPC;
import com.NachOS.Modules.ConditionVariable.ConditionVariable;
import com.NachOS.Modules.ConditionVariable.IConditionVariable;
import com.NachOS.Modules.FileSystem.Catalog;
import com.NachOS.Modules.FileSystem.FileSystem;
import com.NachOS.Modules.FileSystem.IFileSystem;
import com.NachOS.Modules.Interpreter.Interpreter;
import com.NachOS.Modules.MemoryManagment.RAM;
import com.NachOS.Modules.ProcessManager.ProcessManager;
import com.NachOS.Modules.Processor.IProcessor;
import com.NachOS.Modules.Processor.Processor;
import com.NachOS.Modules.Shell.Shell;


public class Main{
    //I just add this comment for test.
    private static ILoginService loginService;
    private static IUserController userController;
    private static IACLController aclController;

    private static IFileSystem fileSystem;
    private static Catalog catalog;

    private static IConditionVariable conditionVariable;
    private static ProcessManager processManager;

    private static IPC ipc;

    private static RAM ram;

    private static Interpreter interpreter;

    private static IProcessor processor;
        public static void main(String[] args) {
            ram = new RAM();
            processManager = new ProcessManager(ram);
            processManager.setStartingActivePCB();
            ipc = new IPC(processManager, ram);

            userController = new UserController();
            loginService = new LoginService(userController);
            aclController = new ACLController(userController);
            CreateDefaultUsers();

            CreateCatalog();
            fileSystem = new FileSystem(catalog, processManager);

            conditionVariable = new ConditionVariable(processManager);
            interpreter = new Interpreter(processManager, fileSystem, ipc, loginService);
            IProcessor processor = new Processor(processManager, interpreter);
            processor.setInterpreter(interpreter);

            Shell shell = new Shell(userController, fileSystem, ram, processor, aclController, processManager, loginService, ipc, interpreter);
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
