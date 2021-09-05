package com.clj.fastble.exception;

//import android.bluetooth.BluetoothGatt;


public class ConnectException<T> extends BleException {

    private T bluetoothGatt;
    private int gattStatus;

    public ConnectException(T bluetoothGatt, int gattStatus) {
        super(ERROR_CODE_GATT, "Gatt Exception Occurred! ");
        this.bluetoothGatt = bluetoothGatt;
        this.gattStatus = gattStatus;
    }

    public int getGattStatus() {
        return gattStatus;
    }

    public ConnectException setGattStatus(int gattStatus) {
        this.gattStatus = gattStatus;
        return this;
    }

    public T getBluetoothGatt() {
        return bluetoothGatt;
    }

    public ConnectException setBluetoothGatt(T bluetoothGatt) {
        this.bluetoothGatt = bluetoothGatt;
        return this;
    }

    @Override
    public String toString() {
        return "ConnectException{" +
               "gattStatus=" + gattStatus +
               ", bluetoothGatt=" + bluetoothGatt +
               "} " + super.toString();
    }
}
