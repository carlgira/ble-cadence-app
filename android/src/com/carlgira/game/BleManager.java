package com.carlgira.game;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.util.Log;

import com.clj.fastble.bluetooth.BleBluetooth;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.IBleDevice;
import com.clj.fastble.data.IBleManager;

public class BleManager implements IBleManager {

    private AndroidLauncher launcher;

    @Override
    public void disconnect(IBleDevice device, BleGattCallback callback) {

    }

    @Override
    public void subscribeToCharacteristic(IBleDevice device,String serviceUUId, String charUuid, BleNotifyCallback callback) {

        com.clj.fastble.BleManager.getInstance().notify(
                device,
                serviceUUId,
                charUuid,
                callback);
    }

    @Override
    public void scan(String uuid, BleScanCallback callback) {
        this.launcher.checkPermissions(uuid, callback);
    }

    @Override
    public void connect(IBleDevice device, BleGattCallback callback) {
       com.clj.fastble.BleManager.getInstance().connect(device, callback);

       com.clj.fastble.BleManager.getInstance().getMultipleBluetoothController().removeConnectingBle(com.clj.fastble.BleManager.getInstance().getBleBluetooth(device));
       com.clj.fastble.BleManager.getInstance().getMultipleBluetoothController().addBleBluetooth(com.clj.fastble.BleManager.getInstance().getBleBluetooth(device));

    }


    public void disconnectAllDevice(){
        com.clj.fastble.BleManager.getInstance().disconnectAllDevice();
    }

    public void destroy(){
        com.clj.fastble.BleManager.getInstance().destroy();
    }

    public void init(Application application) {

        com.clj.fastble.BleManager.getInstance().init(application);
        com.clj.fastble.BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

    public void setAndroidApp(AndroidLauncher launcher){
        this.launcher = launcher;
    }
}


