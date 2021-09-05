package com.carlgira.game.comm;


import com.clj.fastble.data.IBleDevice;

public interface Observer {

    void disConnected(IBleDevice bleDevice);
}
