package com.example.googleble.interfaceFragmentActivity;

import com.example.googleble.CustomObjects.CustBluetootDevices;

public interface SendDataToBleDevice {
    public void parseDataToBleDevice(CustBluetootDevices custBluetootDevices, byte[] dataToSend);
}
