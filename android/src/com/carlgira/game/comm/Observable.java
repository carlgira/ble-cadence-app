package com.carlgira.game.comm;

import com.clj.fastble.data.IBleDevice;

public interface Observable {

    void addObserver(Observer obj);

    void deleteObserver(Observer obj);

    void notifyObserver(IBleDevice bleDevice);
}
