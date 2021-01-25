package com.example.googleble.ByteConversionPackage;

public class ByteConversionHelper {

    public static String convertByteArrayToHexString(byte[] inputValue){
        String hexValue="";
        for (byte b : inputValue) {
            String st = String.format("%02X", b);
            System.out.print(st);
            hexValue=st;
        }
        return hexValue;
    }

}
