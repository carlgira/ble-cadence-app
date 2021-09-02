package com.carlgira.game.ble;

public interface IBleDevice<T> {

    String getName();

    String getMac();

    String getKey();

    T getDevice();

    void setDevice(T device);

    byte[] getScanRecord();

    void setScanRecord(byte[] scanRecord);

    int getRssi();

    void setRssi(int rssi);

    long getTimestampNanos();

    void setTimestampNanos(long timestampNanos);
}
