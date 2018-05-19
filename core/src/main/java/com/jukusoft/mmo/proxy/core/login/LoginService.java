package com.jukusoft.mmo.proxy.core.login;

import com.jukusoft.mmo.proxy.core.service.IService;

public interface LoginService extends IService {

    /**
    * check credentials of user
     *
     * @param username name of user
     * @param password password of user
     *
     * @return userID or -1, if credentials are wrong
    */
    public int login (String username, String password);

}
