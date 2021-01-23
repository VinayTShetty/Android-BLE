package com.example.googleble.CustomObjects;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.Nullable;

public class CustBluetootDevices {
    private String bleAddress;
    private String deviceName;
    private BluetoothDevice bluetoothDevice;
    private boolean isConnected;
    private String dataObtained;

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

    public String getDataObtained() {
        return dataObtained;
    }

    public void setDataObtained(String dataObtained) {
        this.dataObtained = dataObtained;
    }

    /**
     *
     * equals method is used to make the Unique when the Device is added to the Arraylist.
     * By overriding the equals method.
     *
     */
    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof CustBluetootDevices && (this.bleAddress.equalsIgnoreCase(((CustBluetootDevices) obj).bleAddress));
    }

}
