package com.carlgira.game.ble;



import com.carlgira.game.base.Callback;

import java.util.List;

public interface IBleManager<T> {

     List<IBleDevice<T>> scan(String uuid);

     void checkPermissions(Callback callback);

}

