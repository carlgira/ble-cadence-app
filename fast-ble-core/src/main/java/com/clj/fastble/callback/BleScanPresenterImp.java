package com.clj.fastble.callback;

import com.clj.fastble.data.IBleDevice;

public interface BleScanPresenterImp {

    void onScanStarted(boolean success);

    void onScanning(IBleDevice bleDevice);

}
