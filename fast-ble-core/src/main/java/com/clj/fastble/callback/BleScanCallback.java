package com.clj.fastble.callback;

import com.clj.fastble.data.IBleDevice;

import java.util.List;

public abstract class BleScanCallback implements BleScanPresenterImp {

    public abstract void onScanFinished(List<IBleDevice> scanResultList);

    public void onLeScan(IBleDevice bleDevice) {
    }
}
