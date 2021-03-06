package com.jukusoft.mmo.proxy.core;

import com.jukusoft.mmo.proxy.core.frontend.IFrontend;
import com.jukusoft.mmo.proxy.core.service.IService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProxyServer {

    //map which stores services
    protected Map<Class<?>,IService> serviceMap = new ConcurrentHashMap<>();

    //list with all frontend modules
    protected Map<Class<?>,IFrontend> frontendMap = new HashMap<>();

    //unix timestamp when server was started
    protected long started = 0;

    public ProxyServer () {
        this.started = System.currentTimeMillis();
    }

    public <T extends IService> void addService (T obj, Class<T> cls) {
        if (obj == null) {
            throw new NullPointerException("service cannot be null.");
        }

        this.serviceMap.put(cls, obj);
    }

    public <T extends IService> void removeService (Class<T> cls) {
        this.serviceMap.remove(cls);
    }

    public <T extends IService> T getService (Class<T> cls) {
        IService service = this.serviceMap.get(cls);

        if (service == null) {
            throw new NullPointerException("service " + cls.getSimpleName() + " doesnt exists.");
        }

        return cls.cast(service);
    }

    public Set<Class<?>> listServiceClasses () {
        return this.serviceMap.keySet();
    }

    public <T extends IFrontend> void addFrontend (T obj, Class<T> cls) {
        if (obj == null) {
            throw new NullPointerException("frontend instance cannot be null.");
        }

        //initialize frontend
        obj.init(this);

        //start frontend
        obj.start();

        this.frontendMap.putIfAbsent(cls, obj);
    }

    public <T extends IFrontend> void removeFrontend (Class<T> cls) {
        IFrontend frontend = this.frontendMap.get(cls);

        if (frontend != null) {
            //stop frontend
            frontend.stop();
        }

        //remove frontend from map
        this.frontendMap.remove(cls);
    }

    public List<IFrontend> listFrontends () {
        List<IFrontend> frontends = new ArrayList<>();

        for (Map.Entry<Class<?>,IFrontend> entry : this.frontendMap.entrySet()) {
            frontends.add(entry.getValue());
        }

        return frontends;
    }

    public long getUptimeInSeconds () {
        return (System.currentTimeMillis() - this.started) / 1000l;
    }

    public void log (Level level, String msg) {
        Logger.getAnonymousLogger().log(level, msg);
    }

}
