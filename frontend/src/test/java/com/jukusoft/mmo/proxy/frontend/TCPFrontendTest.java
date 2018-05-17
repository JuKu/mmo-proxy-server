package com.jukusoft.mmo.proxy.frontend;

import com.jukusoft.mmo.proxy.core.ProxyServer;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(VertxUnitRunner.class)
public class TCPFrontendTest {

    protected static Vertx vertx;

    @BeforeClass
    public static void before(TestContext context) {
        vertx = Vertx.vertx();

        // Register the context exception handler
        vertx.exceptionHandler(context.exceptionHandler());

        /*server =
                vertx.createHttpServer().requestHandler(req -> req.response().end("foo")).
                        listen(8080, context.asyncAssertSuccess());*/
    }

    @AfterClass
    public static void after(TestContext context) {
        vertx.close();
    }

    @Test
    public void testConstructor () {
        //https://github.com/vert-x3/vertx-examples/blob/master/unit-examples/src/test/java/io/vertx/example/unit/test/JUnitAndHamcrestTest.java

        new TCPFrontend(vertx, 5000, 1);
    }

    @Test
    public void testGetter () {
        TCPFrontend frontend = new TCPFrontend(vertx, 5000, 1);
        assertNotNull(frontend.getName());
        assertNotNull(frontend.getDescription());
        assertNotNull(frontend.getPort());
    }

    @Test
    public void testStartAndStop () throws InterruptedException {
        TCPFrontend frontend = new TCPFrontend(vertx, 5000, 1);

        ProxyServer server = new ProxyServer();
        frontend.init(server);

        frontend.start();

        Thread.sleep(1000l);

        frontend.stop();
    }

}
