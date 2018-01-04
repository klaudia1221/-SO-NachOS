package com.NachOS.Modules.ACL;

import com.NachOS.Modules.ACL.Interfaces.ILoginService;
import com.NachOS.Modules.ACL.Interfaces.IUserController;

public class LoginService implements ILoginService {
    private IUserController userController;
    private User loggedUser;

    public LoginService(IUserController userController){
        this.userController = userController;
    }

    @Override
    public User getLoggedUser() {
        return loggedUser;
    }

    @Override
    public void loginUser(String name) throws Exception {
        this.loggedUser = userController.getUser(name);
    }
}
