package com.jukusoft.mmo.proxy.frontend;

import com.jukusoft.mmo.proxy.core.ProxyServer;
import com.jukusoft.mmo.proxy.core.frontend.IFrontend;
import com.jukusoft.mmo.proxy.core.service.connection.Connection;
import com.jukusoft.mmo.proxy.core.service.connection.IConnectionManager;
import com.jukusoft.mmo.proxy.core.service.session.ISessionManager;
import com.jukusoft.mmo.proxy.core.service.firewall.IFirewall;
import com.jukusoft.mmo.proxy.core.service.session.Session;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPFrontend implements IFrontend {

    //port tcp server is listen on
    protected final int port;

    protected Vertx vertx = null;
    protected int nOfThreads = 0;

    //instance of vert.x tcp server
    protected List<NetServer> servers = null;

    protected ProxyServer proxyServer = null;

    protected static final Logger LOGGER = Logger.getLogger("TCPFrontend");

    public TCPFrontend (Vertx vertx, int port, int threads) {
        this.vertx = vertx;
        this.port = port;
        this.nOfThreads = threads;
    }

    @Override
    public String getName() {
        return "TCP Frontend";
    }

    @Override
    public String getDescription() {
        return "TCP Proxy Frontend";
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public void init(ProxyServer server) {
        this.proxyServer = proxyServer;
    }

    @Override
    public void start() {
        NetServerOptions options = new NetServerOptions().setPort(getPort());

        //Scaling - sharing TCP servers, see https://vertx.io/docs/vertx-core/java/#_scaling_sharing_tcp_servers
        for (int i = 0; i < this.nOfThreads; i++) {
            //create new tcp server
            NetServer server = this.vertx.createNetServer(options);

            server.connectHandler(socket -> {
                //get firewall service
                IFirewall firewall = proxyServer.getService(IFirewall.class);

                //get connection manager
                IConnectionManager connectionManager = proxyServer.getService(IConnectionManager.class);

                //get session manager
                ISessionManager sessionManager = proxyServer.getService(ISessionManager.class);

                String ip = socket.remoteAddress().host();

                //check, if ip is blacklisted
                if (firewall.isBlacklisted(ip)) {
                    proxyServer.log(Level.WARNING, "ip '" + ip + "' has tried to connect to server, but is blacklisted by firewall, so connection was refused.");

                    //close socket
                    socket.close();

                    return;
                }

                //create new session
                final Session session = sessionManager.createSession(ip, socket.remoteAddress().port());

                proxyServer.log(Level.INFO, "new connection, ip: " + ip + " (sessionID: " + session.getSessionID() + ").");

                //add connection to connection manager
                Connection conn = connectionManager.addConnection(ip, session);

                conn.setReceiver(buffer -> {
                    socket.write(buffer);
                });

                socket.handler(buffer -> {
                    System.out.println("[" + ip + "] received some bytes: " + buffer.length());

                    conn.send(buffer);
                });

                socket.closeHandler(v -> {
                    proxyServer.log(Level.WARNING, "[" + ip + "] The socket has been closed");

                    //close connections to game servers
                    conn.close();

                    //close session
                    sessionManager.closeSession(session.getSessionID());
                });
            });

            //start server
            server.listen(this.getPort());

            servers.add(server);
        }
    }

    @Override
    public void stop() {
        for (NetServer server : servers) {
            //shutdown tcp server
            server.close();
        }
    }

}
