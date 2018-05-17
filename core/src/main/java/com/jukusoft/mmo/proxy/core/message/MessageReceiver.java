package com.jukusoft.mmo.proxy.core.message;

@FunctionalInterface
public interface MessageReceiver<T> {

    public void receive (T buffer);

}
