package com.jukusoft.mmo.proxy.management;

import com.jukusoft.mmo.proxy.core.ProxyServer;
import com.jukusoft.mmo.proxy.core.frontend.IFrontend;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
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

        AuthProvider authProvider = new AuthProvider() {
            @Override
            public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
                String username = authInfo.getString("username");
                String password = authInfo.getString("password");

                if (username.equals("admin") && password.equals("admin")) {
                    resultHandler.handle(Future.succeededFuture(null));
                } else {
                    resultHandler.handle(Future.failedFuture("wrong credentials!"));
                }
            }
        };

        //AuthHandler basicAuthHandler = BasicAuthHandler.create(authProvider);

        router.route().handler(UserSessionHandler.create(authProvider));

        //add cookie handler
        router.route().handler(CookieHandler.create());

        //add routes
        this.addRoutes(router);

        this.httpServer.requestHandler(router::accept).listen(this.getPort());
    }

    protected void addRoutes (Router router) {
        Route route1 = router.route("/").handler(routingContext -> {

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

            response.end(json.toString());

            // Call the next matching route after a 5 second delay
            //routingContext.vertx().setTimer(5000, tid -> routingContext.next());
        });
    }

    @Override
    public void stop() {

    }

}
