package com.NachOS.Modules.ACL.Interfaces;

import com.NachOS.Modules.ACL.User;

public interface ILoginService {
    User getLoggedUser();
    void loginUser(String name) throws Exception;
}
