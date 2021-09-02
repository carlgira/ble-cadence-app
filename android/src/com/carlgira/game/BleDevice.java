package com.carlgira.game;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import com.carlgira.game.ble.IBleDevice;

public class BleDevice implements IBleDevice<BluetoothDevice>, Parcelable {

        private BluetoothDevice mDevice;
        private byte[] mScanRecord;
        private int mRssi;
        private long mTimestampNanos;

        public BleDevice(BluetoothDevice device) {
            mDevice = device;
        }

        public BleDevice(com.clj.fastble.data.BleDevice device){
            mDevice = device.getDevice();
            mScanRecord = device.getScanRecord();
            mRssi = device.getRssi();
            mTimestampNanos = device.getTimestampNanos();
        }

        public BleDevice(BluetoothDevice device, int rssi, byte[] scanRecord, long timestampNanos) {
            mDevice = device;
            mScanRecord = scanRecord;
            mRssi = rssi;
            mTimestampNanos = timestampNanos;
        }

        protected BleDevice(Parcel in) {
            mDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
            mScanRecord = in.createByteArray();
            mRssi = in.readInt();
            mTimestampNanos = in.readLong();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(mDevice, flags);
            dest.writeByteArray(mScanRecord);
            dest.writeInt(mRssi);
            dest.writeLong(mTimestampNanos);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<BleDevice> CREATOR = new Creator<BleDevice>() {
            @Override
            public BleDevice createFromParcel(Parcel in) {
                return new BleDevice(in);
            }

            @Override
            public BleDevice[] newArray(int size) {
                return new BleDevice[size];
            }
        };

        public String getName() {
            if (mDevice != null){
                return mDevice.getName();
            }
            return null;
        }

        public String getMac() {
            if (mDevice != null)
                return mDevice.getAddress();
            return null;
        }

        public String getKey() {
            if (mDevice != null)
                return mDevice.getName() + mDevice.getAddress();
            return "";
        }

        public BluetoothDevice getDevice() {
            return mDevice;
        }

        @Override
        public void setDevice(BluetoothDevice device) {
            this.mDevice = device;
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

