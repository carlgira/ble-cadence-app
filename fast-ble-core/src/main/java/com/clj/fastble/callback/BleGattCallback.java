package com.clj.fastble.callback;


import com.clj.fastble.data.IBleDevice;
import com.clj.fastble.exception.BleException;


public abstract class BleGattCallback<T> {

    public abstract void onStartConnect();

    public abstract void onConnectFail(IBleDevice bleDevice, BleException exception);

    public abstract void onConnectSuccess(IBleDevice bleDevice, T gatt, int status);

    public abstract void onDisConnected(boolean isActiveDisConnected, IBleDevice device, T gatt, int status);

    public static class DummyBleGattCallback extends BleGattCallback{

        @Override
        public void onStartConnect() {

        }

        @Override
        public void onConnectFail(IBleDevice bleDevice, BleException exception) {

        }

        @Override
        public void onConnectSuccess(IBleDevice bleDevice, Object gatt, int status) {

        }

        @Override
        public void onDisConnected(boolean isActiveDisConnected, IBleDevice device, Object gatt, int status) {

        }
    }

}