package com.example.googleble.ByteConversionPackage;

public class ByteConversionHelper {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String convertByteArrayToHexString(byte[] inputValue){
        String hexValue="";
        for (byte b : inputValue) {
            String st = String.format("%02X", b);
            System.out.print(st);
            hexValue=st;
        }
        return hexValue;
    }

    public static byte[] convert_LongTo_4_bytes(long value){
        byte [] data = new byte[4];
        data[3] = (byte) value;
        data[2] = (byte) (value >>> 8);
        data[1] = (byte) (value >>> 16);
        data[0] = (byte) (value >>> 32);
        return data;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
