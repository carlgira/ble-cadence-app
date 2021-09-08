package com.carlgira.game.desktop;

import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.IBleDevice;
import com.clj.fastble.data.IBleManager;

import java.util.ArrayList;
import java.util.List;

public class BleManager implements IBleManager {

    @Override
    public void scan(String uuid, BleScanCallback callback) {
        List<IBleDevice> dummyDevices = new ArrayList<>();
        dummyDevices.add(new DummyDevice("x237-ty"));
        dummyDevices.add(new DummyDevice("x238-er"));
        callback.onScanFinished(dummyDevices);
    }

    @Override
    public void connect(IBleDevice device, BleGattCallback callback) {

    }

    @Override
    public void disconnect(IBleDevice device, BleGattCallback callback) {

    }

    @Override
    public void subscribeToCharacteristic(IBleDevice device, String servUuid, String charUuid, BleNotifyCallback callback) {

    }


}
