# mmo-proxy-server
A proxy server (tooling) for a mmo game. This is only a tool, not a complete game!

Maps are stored on **FTP Server**.

Proxy Server has to be **transparent**.

## Configuration Management

Proxy Server is auto configured by [Hazelcast](http://hazelcast.org) and [MySQL](https://www.mysql.com/de/)

## Requirements

  - LDAP Server (with kerberos)
  - [MySQL Server](https://www.mysql.com/de/)
  - [Hazelcast Server](http://hazelcast.org)
  - FTP Server (stores maps and map data)

## Network Protocol

  - 1 byte type
  - 1 byte extendedType (will not be parsed by proxy server
  - 1 short version (protocol version)
  - payload data (redirected to game servers)

**Type 0x01 is reserved for proxy - sector Server communication**.

### Reserved types

  - 0x01 reserved for proxy - game server communication (client is not allowed to send such messages)
  - 0x02 authorization (registration, login, create character, character selection and so on)
  - 0x03 movement (if connection failes between proxy - game server, this messages will be dropped)

## Modules

  - Core (Config, Login, RSA Encryption, Firewall, ...)
  - Frontend (TCP game frontend)
  - Backend
      * sector server backend
      * login server backend (Registration, Login and so on)
  - Management Module (HTTP Rest Api)
      * list logged in users
      * list frontends with status
      * list available backends with status

## Services / Components

There are several Services, e.q. for logging and login.

  - logging
  - login
  - chat (should be also run as standalone version)
  - FTP Server (**to get maps from FTP server**)
  - data storage (mysql)

## Frontends

Proxy Server can have several frontends with different types.

  - TCP game frontend
  - UDP game frontend
  - HTTP/2 management frontend

## Backends

  - several Game Server Backend (redirects messages to game servers, one backend per game server or vert.x backend for event queue)