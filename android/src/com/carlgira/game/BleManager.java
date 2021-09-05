package com.carlgira.game;

import android.app.Application;

import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.IBleDevice;
import com.clj.fastble.data.IBleManager;

public class BleManager implements IBleManager {

    private AndroidLauncher launcher;

    @Override
    public void disconnect(IBleDevice device, BleGattCallback callback) {

    }

    @Override
    public void scan(String uuid, BleScanCallback callback) {
        this.launcher.checkPermissions(uuid, callback);
    }

    @Override
    public void connect(IBleDevice device, BleGattCallback callback) {

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


