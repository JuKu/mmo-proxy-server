package com.jukusoft.mmo.proxy.frontend;

import com.jukusoft.mmo.proxy.core.ProxyServer;
import com.jukusoft.mmo.proxy.core.config.Config;
import com.jukusoft.mmo.proxy.core.frontend.IFrontend;
import com.jukusoft.mmo.proxy.core.logger.MMOLogger;
import com.jukusoft.mmo.proxy.core.service.connection.ClientConnection;
import com.jukusoft.mmo.proxy.core.service.connection.GSConnectionManager;
import com.jukusoft.mmo.proxy.core.service.connection.IConnectionManager;
import com.jukusoft.mmo.proxy.core.service.session.ISessionManager;
import com.jukusoft.mmo.proxy.core.service.firewall.IFirewall;
import com.jukusoft.mmo.proxy.core.service.session.Session;
import com.jukusoft.mmo.proxy.core.stream.BufferStream;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPFrontend implements IFrontend {

    //port tcp server is listen on
    protected final int port;

    protected Vertx vertx = null;
    protected int nOfThreads = 0;

    //instance of vert.x tcp server
    protected List<NetServer> servers = new ArrayList<>();

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
        this.proxyServer = server;
    }

    @Override
    public void start() {
        NetServerOptions options = new NetServerOptions().setPort(getPort());

        //Scaling - sharing TCP servers, see https://vertx.io/docs/vertx-core/java/#_scaling_sharing_tcp_servers
        for (int i = 0; i < this.nOfThreads; i++) {
            //create new tcp server
            NetServer server = this.vertx.createNetServer(options);

            server.connectHandler(socket -> {
                //create buffer stream
                BufferStream bufferStream = new BufferStream(socket, socket);

                //pause reading data
                bufferStream.pause();

                //get firewall service
                IFirewall firewall = proxyServer.getService(IFirewall.class);

                //get connection manager
                IConnectionManager connectionManager = proxyServer.getService(IConnectionManager.class);

                //get session manager
                ISessionManager sessionManager = proxyServer.getService(ISessionManager.class);

                final String ip = socket.remoteAddress().host();
                final int clientPort = socket.remoteAddress().port();

                //check, if ip is blacklisted
                if (firewall.isBlacklisted(ip)) {
                    MMOLogger.log(Level.WARNING, "ip '" + ip + "' has tried to connect to server, but is blacklisted by firewall, so connection was refused.");

                    //close socket
                    bufferStream.end();
                    socket.close();

                    return;
                }

                //create new session
                final Session session = sessionManager.createSession(ip, clientPort);

                //create new connection
                ClientConnection conn = new ClientConnection();

                //add connection to connection manager
                connectionManager.addConnection(ip, clientPort, conn, proxyServer.getService(GSConnectionManager.class));

                //set receiver which sends message to client
                conn.setReceiver(buffer -> {
                    if (buffer.getByte(0) == Config.MSG_CLOSE_CONN) {
                        //close socket
                        closeConnection(socket, conn, sessionManager, session, connectionManager);

                        return;
                    }

                    //send message to client
                    bufferStream.write(buffer);
                });

                bufferStream.handler(buffer -> {
                    //avoid logging rtt messages to reduce spam in logs
                    if (buffer.getByte(0) != Config.MSG_TYPE_PROXY && buffer.getByte(1) != Config.MSG_EXTENDED_TYPE_RTT) {
                        //message was received from client
                        MMOLogger.log(Level.SEVERE, "[" + ip + "] received some bytes: " + buffer.length());
                    }

                    conn.receive(buffer);
                });

                bufferStream.endHandler(v -> closeConnection(socket, conn, sessionManager, session, connectionManager));
            });

            //start server
            server.listen(this.getPort());

            servers.add(server);
        }
    }

    protected void closeConnection (NetSocket socket, ClientConnection conn, ISessionManager sessionManager, Session session, IConnectionManager connectionManager) {
        MMOLogger.log(Level.WARNING, "[" + socket.remoteAddress().host() + "] The socket has been closed");

        //close connections to game servers
        conn.close();

        //close session
        sessionManager.closeSession(session.getSessionID());

        //remove connection
        connectionManager.removeConnection(conn);

        socket.close();
    }

    @Override
    public void stop() {
        for (NetServer server : servers) {
            //shutdown tcp server
            server.close();
        }
    }

}
