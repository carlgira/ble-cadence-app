package com.carlgira.game;

import android.app.Application;

import com.carlgira.game.base.Callback;
import com.carlgira.game.ble.IBleManager;

import java.util.ArrayList;
import java.util.List;

public class BleManager implements IBleManager {

    private AndroidLauncher launcher;

    @Override
    public List<BleDevice> scan(String uuid) {
        List<BleDevice> devices = new ArrayList<>();
        for(int i =0;i< launcher.mDeviceAdapter.getCount();i++){
            devices.add(new BleDevice(launcher.mDeviceAdapter.getItem(i)));
        }
        return devices;
    }

    @Override
    public void checkPermissions(Callback callback) {
        launcher.checkPermissions(callback);
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


