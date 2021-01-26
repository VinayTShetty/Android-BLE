package com.example.googleble.BLE_packets;

public class BleAuthenication {
    public static byte[] WriteValue01() {
        byte byte_value[] = new byte[1];
        byte_value[0]=0X01;
        return byte_value;
    }
}
