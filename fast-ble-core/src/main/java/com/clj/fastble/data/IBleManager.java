package com.clj.fastble.data;

import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;


public interface IBleManager<T> {

    void scan(String uuid, BleScanCallback callback);

    void connect(IBleDevice<T> device, BleGattCallback callback);

    void disconnect(IBleDevice<T> device, BleGattCallback callback);

    void subscribeToCharacteristic(IBleDevice<T> device, String servUuid, String charUuid, BleNotifyCallback callback);

}
