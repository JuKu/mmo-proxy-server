package com.jukusoft.mmo.proxy.management;

import com.jukusoft.mmo.proxy.core.ProxyServer;
import com.jukusoft.mmo.proxy.core.frontend.IFrontend;
import com.jukusoft.mmo.proxy.core.service.connection.IConnectionManager;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;

public class ManagementFrontend implements IFrontend {

    protected static final String MODULE_TITLE = "Management Module";
    protected static final String MODULE_DESCRIPTION = "Proxy Management Module";

    protected Vertx vertx = null;
    protected int port = 0;

    protected HttpServer httpServer = null;

    protected ProxyServer proxyServer = null;

    protected Route route1 = null;

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

        //create router
        Router router = Router.router(vertx);

        //http basic auth
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

        //add cookie handler
        router.route().handler(CookieHandler.create());

        //add routes
        this.addRoutes(router);

        this.httpServer.requestHandler(router::accept).listen(this.getPort());
    }

    protected void addRoutes (Router router) {
        this.route1 = router.route("/").handler(routingContext -> {

            HttpServerResponse response = routingContext.response();
            response.putHeader("Content-Type", "application/json");

            JsonObject json = new JsonObject();
            json.put("uptime_in_seconds", this.proxyServer.getUptimeInSeconds());

            //list frontends
            JsonArray array = new JsonArray();

            for (IFrontend frontend : proxyServer.listFrontends()) {
                JsonObject json1 = new JsonObject();
                json1.put("class", frontend.getClass().getCanonicalName());
                json1.put("name", frontend.getName());
                json1.put("description", frontend.getDescription());
                json1.put("port", frontend.getPort());

                array.add(json1);
            }

            json.put("frontends", array);

            json.put("opened_frontend_connections", proxyServer.getService(IConnectionManager.class).countOpenFrontendConnections());
            json.put("opened_backend_connections", proxyServer.getService(IConnectionManager.class).countOpenBackendConnections());

            response.end(json.toString());
        });
    }

    @Override
    public void stop() {
        this.httpServer.close();
    }

}
