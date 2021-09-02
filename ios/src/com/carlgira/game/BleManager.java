package com.carlgira.game;

import com.carlgira.game.base.Callback;
import com.carlgira.game.ble.IBleDevice;
import com.carlgira.game.ble.IBleManager;

import java.util.List;

public class BleManager implements IBleManager {

    private IOSLauncher launcher;
    @Override
    public List<IBleDevice> scan(String uuid) {
        return null;
    }


    @Override
    public void checkPermissions(Callback callback) {
        this.launcher.checkPermissions(callback);
    }

    public void setIOSApp(IOSLauncher launcher){
        this.launcher = launcher;
    }
}
