package com.jukusoft.mmo.proxy.core.frontend;

import com.jukusoft.mmo.proxy.core.ProxyServer;

public interface IFrontend {

    /**
    * get name of frontend
    */
    public String getName ();

    /**
    * get description of frontend, e.q. "management module" or "tcp frontend"
    */
    public String getDescription ();

    /**
    * get port frontend listen on
    */
    public int getPort ();

    /**
    * initialize frontend
    */
    public void init (ProxyServer server);

    public void start ();

    public void stop ();

}
