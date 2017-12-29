package com.BamoOS.Modules.ACL.Interfaces;

import com.BamoOS.Modules.ACL.User;

public interface ILoginService {
    User getLoggedUser();
    void loginUser(String name) throws Exception;
}
