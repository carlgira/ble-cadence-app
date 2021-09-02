package com.carlgira.game.desktop;

import com.carlgira.game.base.Callback;
import com.carlgira.game.ble.IBleDevice;
import com.carlgira.game.ble.IBleManager;

import java.util.ArrayList;
import java.util.List;

public class BleManager implements IBleManager {
    @Override
    public List<IBleDevice> scan(String uuid) {
        return new ArrayList<>();
    }

    @Override
    public void checkPermissions(Callback callback) {

    }
}
