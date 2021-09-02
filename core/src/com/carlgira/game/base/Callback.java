package com.carlgira.game.base;

import com.carlgira.game.ble.IBleDevice;

import java.util.List;

public interface Callback<T> {
    void call(List<T> devices);

    void call(IBleDevice device);
}
