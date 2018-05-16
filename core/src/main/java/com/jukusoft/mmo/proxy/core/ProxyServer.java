package com.jukusoft.mmo.proxy.core;

import com.jukusoft.mmo.proxy.core.service.IService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProxyServer {

    //map which stores services
    protected Map<Class<?>,IService> serviceMap = new ConcurrentHashMap<>();

    public ProxyServer () {
        //
    }

    public <T extends IService> void addService (T obj, Class<T> cls) {
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

}
