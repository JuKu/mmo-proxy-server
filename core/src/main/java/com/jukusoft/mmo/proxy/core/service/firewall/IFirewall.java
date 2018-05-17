package com.jukusoft.mmo.proxy.core.service.firewall;

import com.jukusoft.mmo.proxy.core.service.IService;

public interface IFirewall extends IService {

    public boolean isBlacklisted (String ip);

}
