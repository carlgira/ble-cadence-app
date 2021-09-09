package com.carlgira.game.desktop;

import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.IBleDevice;
import com.clj.fastble.data.IBleController;

import java.util.ArrayList;
import java.util.List;

public class BleController extends IBleController {

    @Override
    public void scan(BleScanCallback callback) {
        List<IBleDevice> dummyDevices = new ArrayList<>();
        callback.onScanning(new DummyDevice("x237-ty"));
        callback.onScanning(new DummyDevice("x238-er"));
        callback.onScanFinished(dummyDevices);
    }

    @Override
    public void stopScan() {

    }

    @Override
    public boolean isConnected(IBleDevice device) {
        return true;
    }

    @Override
    public void connect(IBleDevice device, BleGattCallback callback) {
        callback.onConnectSuccess(device, null, 0);
    }

    @Override
    public void disconnect(IBleDevice device, BleGattCallback callback) {
        callback.onDisConnected(true, device, null ,0);
    }

    @Override
    public void notify(IBleDevice device, BleNotifyCallback callback) {
        callback.onNotifySuccess();
        callback.onCharacteristicChanged(new byte[]{2,4,13,65,-54});
    }

    @Override
    public void stopNotify(IBleDevice device) {

    }


}
