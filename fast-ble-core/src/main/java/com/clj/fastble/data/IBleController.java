package com.clj.fastble.data;

import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;

public abstract class IBleController<T> {

    protected String serviceUUID;
    protected String characteristicUUID;

    abstract public void scan(BleScanCallback callback);

    abstract public void stopScan();

    abstract public boolean isConnected(IBleDevice<T> device);

    abstract public void connect(IBleDevice<T> device, BleGattCallback callback);

    abstract public void disconnect(IBleDevice<T> device, BleGattCallback callback);

    abstract public void notify(IBleDevice<T> device, BleNotifyCallback callback);

    abstract public void stopNotify(IBleDevice<T> device);

    public String getServiceUUID() {
        return serviceUUID;
    }

    public void setServiceUUID(String serviceUUID) {
        this.serviceUUID = serviceUUID;
    }

    public String getCharacteristicUUID() {
        return characteristicUUID;
    }

    public void setCharacteristicUUID(String characteristicUUID) {
        this.characteristicUUID = characteristicUUID;
    }

}
