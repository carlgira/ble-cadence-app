package com.carlgira.game;

import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.IBleDevice;
import com.clj.fastble.data.IBleController;
import org.robovm.apple.corebluetooth.CBPeripheral;

public class BleController extends IBleController {

    private IOSLauncher launcher;

    @Override
    public void scan(BleScanCallback callback) {
        this.launcher.checkPermissions(this.serviceUUID, callback);
    }

    @Override
    public void stopScan() {
        this.launcher.cancelScan();
    }

    @Override
    public boolean isConnected(IBleDevice device) {
        return this.launcher.isConnected(device);
    }

    @Override
    public void connect(IBleDevice device, BleGattCallback callback) {
        this.launcher.connectToDevice((CBPeripheral)device.getDevice(), callback);
    }

    @Override
    public void disconnect(IBleDevice device, BleGattCallback callback) {
        this.launcher.disconnectFromDevice();
    }

    @Override
    public void notify(IBleDevice device, BleNotifyCallback callback) {
        this.launcher.subscribeToCharacteristic(this.serviceUUID, this.characteristicUUID, callback);
    }

    @Override
    public void stopNotify(IBleDevice device) {

    }


    public void setIOSApp(IOSLauncher launcher){
        this.launcher = launcher;
    }
}
