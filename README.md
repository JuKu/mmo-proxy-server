# mmo-proxy-server
A proxy server (tooling) for a mmo game. This is only a tool, not a complete game!

## Configuration

Proxy Server is auto configured by [Hazelcast](http://hazelcast.org) and [MySQL](https://www.mysql.com/de/)

## Network Protocol

  - 1 byte type
  - 1 byte extendedType (will not be parsed by proxy server
  - 1 short version (protocol version)
  - payload data (redirected to game servers)

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

## Frontends

Proxy Server can have several frontends with different types.