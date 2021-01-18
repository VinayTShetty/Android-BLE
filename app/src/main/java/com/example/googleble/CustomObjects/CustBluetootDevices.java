package com.example.googleble.CustomObjects;

import android.bluetooth.BluetoothDevice;

public class CustBluetootDevices {
    private String bleAddress;
    private String deviceName;
    private BluetoothDevice bluetoothDevice;
    private boolean isConnected;

    public CustBluetootDevices(String bleAddress, String deviceName, BluetoothDevice bluetoothDevice, boolean isConnected) {
        this.bleAddress = bleAddress;
        this.deviceName = deviceName;
        this.bluetoothDevice = bluetoothDevice;
        this.isConnected = isConnected;
    }

    public CustBluetootDevices() {
    }

    public String getBleAddress() {
        return bleAddress;
    }

    public void setBleAddress(String bleAddress) {
        this.bleAddress = bleAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
