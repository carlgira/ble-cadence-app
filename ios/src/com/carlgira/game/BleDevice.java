package com.carlgira.game;


import com.clj.fastble.data.IBleDevice;

import org.robovm.apple.corebluetooth.CBPeripheral;

public class BleDevice implements IBleDevice<CBPeripheral> {

        private CBPeripheral mDevice;
        private byte[] mScanRecord;
        private int mRssi;
        private long mTimestampNanos;

        public BleDevice(CBPeripheral device) {
            mDevice = device;
        }

        public String getName() {
            if (mDevice != null){
                return mDevice.getName();
            }
            return null;
        }

        public String getMac() {
            if (mDevice != null)
                return "mac";
            return null;
        }

        public String getKey() {
            if (mDevice != null)
                return mDevice.getName(); // + mDevice.get;
            return "";
        }

        public CBPeripheral getDevice() {
            return mDevice;
        }

    @Override
    public void setDevice(CBPeripheral device) {

    }


        public byte[] getScanRecord() {
            return mScanRecord;
        }

        public void setScanRecord(byte[] scanRecord) {
            this.mScanRecord = scanRecord;
        }

        public int getRssi() {
            return mRssi;
        }

        public void setRssi(int rssi) {
            this.mRssi = rssi;
        }

        public long getTimestampNanos() {
            return mTimestampNanos;
        }

        public void setTimestampNanos(long timestampNanos) {
            this.mTimestampNanos = timestampNanos;
        }

    }

