package com.jukusoft.mmo.proxy.management;

import com.jukusoft.mmo.proxy.core.ProxyServer;
import com.jukusoft.mmo.proxy.core.frontend.IFrontend;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.JksOptions;

public class ManagementFrontend implements IFrontend {

    protected static final String MODULE_TITLE = "Management Module";
    protected static final String MODULE_DESCRIPTION = "Proxy Management Module";

    protected Vertx vertx = null;
    protected int port = 0;

    protected HttpServer httpServer = null;

    protected ProxyServer proxyServer = null;

    public ManagementFrontend (Vertx vertx, final int port) {
        this.vertx = vertx;
        this.port = port;
    }

    @Override
    public String getName() {
        return MODULE_TITLE;
    }

    @Override
    public String getDescription() {
        return MODULE_DESCRIPTION;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public void init(ProxyServer server) {
        this.proxyServer = server;
    }

    @Override
    public void start() {
        //setup http/2 server
        HttpServerOptions options = new HttpServerOptions()
                //.setUseAlpn(true)
                //.setSsl(true)
                //.setKeyStoreOptions(new JksOptions().setPath("/path/to/my/keystore"))
                .setLogActivity(true);

        this.httpServer = this.vertx.createHttpServer(options);

        this.httpServer.requestHandler(request -> {

            // This handler gets called for each request that arrives on the server
            HttpServerResponse response = request.response();
            response.putHeader("content-type", "application/json");

            // Write to the response and end it
            response.end("{}");
        });

        this.httpServer.listen(this.getPort());
    }

    @Override
    public void stop() {

    }

}
