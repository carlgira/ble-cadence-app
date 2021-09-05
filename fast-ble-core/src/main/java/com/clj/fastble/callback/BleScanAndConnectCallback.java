package com.clj.fastble.callback;


import com.clj.fastble.data.IBleDevice;

public abstract class BleScanAndConnectCallback extends BleGattCallback implements BleScanPresenterImp {

    public abstract void onScanFinished(IBleDevice scanResult);

    public void onLeScan(IBleDevice bleDevice) {
    }

}
