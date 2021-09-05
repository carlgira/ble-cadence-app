package com.carlgira.game;

import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.IBleDevice;
import com.clj.fastble.data.IBleManager;

import org.robovm.apple.corebluetooth.CBPeripheral;

public class BleManager implements IBleManager {

    private IOSLauncher launcher;


    @Override
    public void scan(String uuid, BleScanCallback callback) {
        this.launcher.checkPermissions(uuid, callback);
    }

    @Override
    public void connect(IBleDevice device, BleGattCallback callback) {
        this.launcher.connectToDevice((CBPeripheral)device.getDevice(), callback);
    }

    @Override
    public void disconnect(IBleDevice device, BleGattCallback callback) {

    }

    public void setIOSApp(IOSLauncher launcher){
        this.launcher = launcher;
    }
}
