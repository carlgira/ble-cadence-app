package com.carlgira.game;

import android.app.Application;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.IBleDevice;
import com.clj.fastble.data.IBleController;

public class BleController extends IBleController {

    private AndroidLauncher launcher;

    @Override
    public void disconnect(IBleDevice device, BleGattCallback callback) {
        BleManager.getInstance().disconnect(device);
    }

    @Override
    public void notify(IBleDevice device, BleNotifyCallback callback) {
        BleManager.getInstance().notify(
                device,
                this.serviceUUID,
                this.characteristicUUID,
                callback);
    }

    @Override
    public void stopNotify(IBleDevice device) {
        BleManager.getInstance().stopNotify(device, serviceUUID, characteristicUUID);
    }

    @Override
    public void scan(BleScanCallback callback) {
        this.launcher.checkPermissions(this.serviceUUID, callback);
    }

    @Override
    public void stopScan() {
        BleManager.getInstance().cancelScan();
    }

    @Override
    public boolean isConnected(IBleDevice device) {
        return  BleManager.getInstance().isConnected(device);
    }

    @Override
    public void connect(IBleDevice device, BleGattCallback callback) {
       BleManager.getInstance().connect(device, callback);

       BleManager.getInstance().getMultipleBluetoothController().removeConnectingBle(BleManager.getInstance().getBleBluetooth(device));
       BleManager.getInstance().getMultipleBluetoothController().addBleBluetooth(BleManager.getInstance().getBleBluetooth(device));

    }


    public void disconnectAllDevice(){
        BleManager.getInstance().disconnectAllDevice();
    }

    public void destroy(){
        BleManager.getInstance().destroy();
    }

    public void init(Application application) {
        BleManager.getInstance().init(application);
    }

    public void setAndroidApp(AndroidLauncher launcher){
        this.launcher = launcher;
    }
}


