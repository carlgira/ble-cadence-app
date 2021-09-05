package com.clj.fastble.callback;



public abstract class BleBaseCallback<T> {

    private String key;
    private T handler;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getHandler() {
        return handler;
    }

    public void setHandler(T handler) {
        this.handler = handler;
    }

}
