# mmo-proxy-server
A proxy server (tooling) for a mmo game. This is only a tool, not a complete game!

## Configuration

Proxy Server is auto configured by [Hazelcast](http://hazelcast.org) and [MySQL](https://www.mysql.com/de/)

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

## Frontends

Proxy Server can have several frontends with different types.