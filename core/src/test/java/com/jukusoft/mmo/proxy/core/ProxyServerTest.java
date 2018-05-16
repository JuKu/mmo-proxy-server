package com.jukusoft.mmo.proxy.core;

import com.jukusoft.mmo.proxy.core.service.DummyService;
import com.jukusoft.mmo.proxy.core.service.IService;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ProxyServerTest {

    @Test
    @DisplayName("Test constructor of proxy server")
    public void testConstructor () {
        new ProxyServer();
    }

    @Test (expected = NullPointerException.class)
    public void testAddNullService () {
        ProxyServer server = new ProxyServer();
        server.addService(null, IService.class);
    }

    @Test
    public void testAddAndGet () {
        ProxyServer server = new ProxyServer();

        IService service = new DummyService();
        server.addService(service, IService.class);

        assertNotNull(server.getService(IService.class));

        IService service1 = server.getService(IService.class);
        assertEquals(true, service.equals(service1));

        server.addService(new DummyService(), IService.class);
        assertNotNull(server.getService(IService.class));
        assertEquals(false, service.equals(server.getService(IService.class)));

        DummyService dummyService = new DummyService();
        server.addService(dummyService, DummyService.class);
        DummyService dummyService1 = server.getService(DummyService.class);
        assertEquals(true, dummyService.equals(dummyService1));
    }

    @Test (expected = NullPointerException.class)
    public void testAddAndGet1 () {
        ProxyServer server = new ProxyServer();

        IService service = new DummyService();
        server.addService(service, IService.class);

        assertNotNull(server.getService(IService.class));

        //this line should throw NullPointerException
        assertNull(server.getService(DummyService.class));
    }

    @Test
    public void testRemoveService () {
        ProxyServer server = new ProxyServer();

        //add service
        IService service = new DummyService();
        server.addService(service, IService.class);

        //remove service
        server.removeService(IService.class);
        server.removeService(DummyService.class);
    }

    @Test
    public void testListServiceNames () {
        ProxyServer server = new ProxyServer();

        assertEquals(0, server.listServiceClasses().size());

        server.addService(new DummyService(), IService.class);
        assertEquals(1, server.listServiceClasses().size());
        assertEquals(false, server.listServiceClasses().contains(DummyService.class));
        assertEquals(true, server.listServiceClasses().contains(IService.class));

        server.addService(new DummyService(), DummyService.class);
        assertEquals(2, server.listServiceClasses().size());
        assertEquals(true, server.listServiceClasses().contains(DummyService.class));
        assertEquals(true, server.listServiceClasses().contains(IService.class));
    }

}
