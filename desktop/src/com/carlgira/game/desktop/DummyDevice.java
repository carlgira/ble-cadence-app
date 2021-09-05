package com.carlgira.game.desktop;


import com.clj.fastble.data.IBleDevice;

public class DummyDevice implements IBleDevice<String> {

    private String device;
    public DummyDevice(String name){
        this.device = name;
    }

    @Override
    public String getName() {
        return device;
    }

    @Override
    public String getMac() {
        return null;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getDevice() {
        return null;
    }

    @Override
    public void setDevice(String device) {

    }

    @Override
    public byte[] getScanRecord() {
        return new byte[0];
    }

    @Override
    public void setScanRecord(byte[] scanRecord) {

    }

    @Override
    public int getRssi() {
        return 0;
    }

    @Override
    public void setRssi(int rssi) {

    }

    @Override
    public long getTimestampNanos() {
        return 0;
    }

    @Override
    public void setTimestampNanos(long timestampNanos) {

    }
}
