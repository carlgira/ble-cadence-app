package com.clj.fastble.callback;


import com.clj.fastble.data.IBleDevice;
import com.clj.fastble.exception.BleException;


public abstract class BleGattCallback<T> {

    public abstract void onStartConnect();

    public abstract void onConnectFail(IBleDevice bleDevice, BleException exception);

    public abstract void onConnectSuccess(IBleDevice bleDevice, T gatt, int status);

    public abstract void onDisConnected(boolean isActiveDisConnected, IBleDevice device, T gatt, int status);

}