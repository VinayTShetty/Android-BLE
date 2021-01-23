package com.example.googleble.Service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.googleble.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import static com.example.googleble.UUID.FirmwareUUID.CLIENT_CHARACTERISTIC_CONFIG;
import static com.example.googleble.UUID.FirmwareUUID.GEO_FENCE_CHARCTERSTICS_UUID;
import static com.example.googleble.UUID.FirmwareUUID.GEO_FENCE_SERVICE_UUID;
import static com.example.googleble.Utility.UtilityHelper.ble_on_off;

public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private static BluetoothGatt mBluetoothGatt;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private int mConnectionState = STATE_DISCONNECTED;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.googleble.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.googleble.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.googleble.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.googleble.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.googleble.le.EXTRA_DATA";

    private Map<String, BluetoothGatt> mutlipleBluetooDeviceGhatt;

    @Override
    public void onCreate() {
        super.onCreate();
        mutlipleBluetooDeviceGhatt = new HashMap<String, BluetoothGatt>();
    }
    /**
     * Service IBinder to get the data to transfer the data.
     */
    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }


    /**
     * Local I Binder.
     */
    public class LocalBinder extends Binder{
         public BluetoothLeService getService(){
            return BluetoothLeService.this;
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            BluetoothDevice bleDevice=gatt.getDevice();
            String bleAddress=bleDevice.getAddress();
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                if(!mutlipleBluetooDeviceGhatt.containsKey(bleAddress)){
                    mutlipleBluetooDeviceGhatt.put(bleAddress,gatt);
                    mutlipleBluetooDeviceGhatt.get(bleAddress).discoverServices();
                }
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                sendDevice_StatusToMainActivty(getResources().getString(R.string.BLUETOOTHLE_SERVICE_BLE_ADDRESS),gatt.getDevice().getAddress(),true);
                broadcastUpdate(intentAction);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                if (mutlipleBluetooDeviceGhatt.containsKey(bleAddress)){
                    BluetoothGatt bluetoothGatt = mutlipleBluetooDeviceGhatt.get(bleAddress);
                    if( bluetoothGatt != null ){
                        bluetoothGatt.close();
                        bluetoothGatt = null;
                    }
                    mutlipleBluetooDeviceGhatt.remove(bleAddress);
                }
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                sendDevice_StatusToMainActivty(getResources().getString(R.string.BLUETOOTHLE_SERVICE_BLE_ADDRESS),gatt.getDevice().getAddress(),false);
                broadcastUpdate(intentAction);
            }else {
                System.out.println("BLE_SERVICE STATUS= "+status);
                System.out.println("BLE_SERVICE NEW STATE = "+newState);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
               enableChartersticNotification(gatt);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.d(TAG,"onCharacteristicWrite Write Type "+characteristic.getWriteType());
            Log.d(TAG,"onCharacteristicWrite Write Type "+characteristic.getValue());
            Log.d(TAG,"onCharacteristicWrite Write Type "+status);
            Log.d(TAG,"onCharacteristicWrite Write Type "+new String(characteristic.getValue()));
            System.out.println("MULTIPLE_BLE_CONNECTION  onConnectionStateChange = BLE_ADDRESS= "+gatt.getDevice().getAddress()+" Gatt hashCode= "+gatt.toString());
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            Log.d(TAG,"onCharacteristicChanged  "+characteristic.getStringValue(2));
            Log.d(TAG,"onCharacteristicChanged  "+new String(characteristic.getValue()));
            System.out.println("onCharacteristicChanged executed");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.d(TAG,"onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.d(TAG,"onDescriptorWrite");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.d(TAG,"onReliableWriteCompleted");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.d(TAG,"onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.d(TAG,"onMtuChanged");
        }
    };

    /**
     * BroadCast Update to send Data to the MainActivity.
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void sendDevice_StatusToMainActivty(final String action,String bleAddress,boolean connectionStatus){
        final Intent intent = new Intent(action);
       intent.putExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_BLE_ADDRESS), bleAddress);
       intent.putExtra(getResources().getString(R.string.CONNECTION_STATUS_BLE_DEVICE),connectionStatus);
       sendBroadcast(intent);
    }

    private void sendDataRecievedFromFirmware(final String action,final String bleaddress,final  byte byteArrayData[]){
        final Intent intent = new Intent(action);
        intent.putExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_BLE_ADDRESS), bleAddress);
        intent.putExtra(getResources().getString(R.string.CONNECTION_STATUS_BLE_DEVICE),connectionStatus);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
        }

        sendBroadcast(intent);
    }


    /**
     * Set Chanrcterstic Notificaiton.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void setCharacteristicNotifications(BluetoothGattCharacteristic characteristic,
                                               boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = null;
        descriptor = characteristic.getDescriptor(
                UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        if (descriptor == null) {
            System.out.println("NOTIFICATION NOT ENABLE");
            return;
        }
        System.out.println("NOTIFICATION ENABLE");
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
     }
    /**
     * Send Data to BLE Devices.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public  void sendDataToBleDevice(byte [] data){
        BluetoothGattService service=mBluetoothGatt.getService(GEO_FENCE_SERVICE_UUID);
        BluetoothGattCharacteristic characteristic= service.getCharacteristic(GEO_FENCE_CHARCTERSTICS_UUID);
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        characteristic.setValue(data);
        boolean status=false;
        status=mBluetoothGatt.writeCharacteristic(characteristic);
        Log.w(TAG, "sendDataToBleDevice "+status);
    }


    public void sendDataToBleDevice(String bleAddress,byte [] data){
        BluetoothGattService service = mutlipleBluetooDeviceGhatt.get(bleAddress).getService(GEO_FENCE_SERVICE_UUID);
        BluetoothGattCharacteristic characteristic= service.getCharacteristic(GEO_FENCE_CHARCTERSTICS_UUID);
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        characteristic.setValue(data);
        boolean status=false;
        BluetoothGatt bluetoothGatt=  mutlipleBluetooDeviceGhatt.get(bleAddress);
        status=bluetoothGatt.writeCharacteristic(characteristic);
        System.out.println("MULTIPLE_BLE_CONNECTION = Status= "+status+" BleAddress= "+bleAddress+" Value= "+new String(data)+" Gatt HashCode= "+bluetoothGatt.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            return false;
        }
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)&& mBluetoothGatt != null) {
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }
        mBluetoothGatt = device.connectGatt(this, false, gattCallback);
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        System.out.println("MULTIPLE_BLE_CONNECTION CONNECTION METHOD = BLE_ADDRESS= "+address+" Gatt hashCode= "+mBluetoothGatt.toString());
        return true;
    }
    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }
    /**
     * We will Recieve data from the Charcterstic.
     * To recieve the data we need to enable the charcterstic
     */
   @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
   private void enableChartersticNotification(BluetoothGatt loc_bluetoothGatt){
       BluetoothGattService service=loc_bluetoothGatt.getService(GEO_FENCE_SERVICE_UUID);
       BluetoothGattCharacteristic characteristic= service.getCharacteristic(GEO_FENCE_CHARCTERSTICS_UUID);
       setCharacteristicNotifications(characteristic,true);
   }

    /**
     *
     * It is used to show the list of charctesrtic in the UI.i.e Expandable list View.
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public List<BluetoothDevice> getListOfConnectedDevices(){
        return    mBluetoothManager.getConnectedDevices(7);
    }


    private boolean checkDeviceIsAlreadyConnected(String bleAddressToCheckConnectionStatus){
        boolean result=false;
        if(ble_on_off()){
            BluetoothDevice device=  mBluetoothAdapter.getRemoteDevice(bleAddressToCheckConnectionStatus);
            int connectionStatus=mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
            if(connectionStatus==BluetoothProfile.STATE_DISCONNECTED){
                    //connect your Device.
            }else if(connectionStatus==BluetoothProfile.STATE_CONNECTED){
                // already connected...
            }
        }else {
            result=false;
        }
        return result;
    }


}

