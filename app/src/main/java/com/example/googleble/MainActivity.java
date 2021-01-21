package com.example.googleble;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.example.googleble.CustomObjects.CustBluetootDevices;
import com.example.googleble.Fragment.FragmentData;
import com.example.googleble.Fragment.FragmentScan;
import com.example.googleble.Service.BluetoothLeService;
import com.example.googleble.interfaceActivityFragment.PassConnectionStatusToFragment;
import com.example.googleble.interfaceActivityFragment.PassScanDeviceToActivity_interface;
import com.example.googleble.interfaceFragmentActivity.DeviceConnectDisconnect;

public class MainActivity extends AppCompatActivity
        implements
        DeviceConnectDisconnect {
    /**
     *BluetoothLeService class Variables.
     */
    public BluetoothLeService mBluetoothLeService;
    String mDeviceAddress="D4:A6:CB:43:B6:70";
/*    Button demoapplicaiton,sendCommand;*/


    /**
     *Scan for the Ble Devices.
     */
    private BluetoothLeScanner bluetoothLeScanner =
            BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    private boolean mScanning;
    private Handler handler = new Handler();
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    /**
     * Activity to Fragment interface
     */
    PassScanDeviceToActivity_interface passScanDeviceToActivity_interface;
    PassConnectionStatusToFragment passConnectionStatusToFragment;
    public static  String SCAN_TAG="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        interfaceIntialization();
        bindBleServiceToMainActivity();
        intializeFragmentManager();
      /*  demoapplicaiton=(Button) findViewById(R.id.demo_applciaiton);
        sendCommand=(Button) findViewById(R.id.send_command);*/

      /*  demoapplicaiton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mBluetoothLeService.connect(mDeviceAddress);
                }
            }
        });

        sendCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String test="smpon";
               mBluetoothLeService.sendDataToBleDevice(test.getBytes());
            }
        });*/
        //scanLeDevice();
        replaceFragmentTransaction(new FragmentScan(),null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(bluetootServiceRecieverData, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bluetootServiceRecieverData);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment.toString().equalsIgnoreCase(new FragmentScan().toString())) {
        }else if(fragment.toString().equalsIgnoreCase(new FragmentData().toString())){
            replaceFragmentTransaction(new FragmentScan(),null);
        }
    }

    /**
     * Code to manage Service life Cycle.
     */
    private final ServiceConnection serviceConnection=new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
          // mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };
    /**
     * BroadCast Reciever tot Recieve Data from BLE Service class..
     */
    private boolean mConnected = false;
    private final BroadcastReceiver bluetootServiceRecieverData=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data=intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            }else if((action!=null)&&(action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_BLE_ADDRESS)))||(action.equalsIgnoreCase(getResources().getString(R.string.CONNECTION_STATUS_BLE_DEVICE)))){
                String bleAddress=intent.getStringExtra((getResources().getString(R.string.BLUETOOTHLE_SERVICE_BLE_ADDRESS)));
                boolean connectionStatus=intent.getBooleanExtra(getResources().getString(R.string.CONNECTION_STATUS_BLE_DEVICE),false);
                passConnectionSucesstoFragmentScanForUIChange(bleAddress,connectionStatus);
            }
        }
        private void passConnectionSucesstoFragmentScanForUIChange(String connectedDeviceAddress,boolean connect_disconnect) {
            if(passConnectionStatusToFragment!=null){
                passConnectionStatusToFragment.connectDisconnect(connectedDeviceAddress,connect_disconnect);
            }
        }
    };
    /**
     * BroadCast Reciever Data Trigger.
     */
    private  IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_BLE_ADDRESS));
        return intentFilter;
    }

    private void bindBleServiceToMainActivity(){
        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * Scan for the BLE Devices.
     */
    public void scanLeDevice() {
        if (!mScanning) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    public void start_stop_scan(){
        if(SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STOPED))||(SCAN_TAG.equalsIgnoreCase(""))){
            startScan();
        }
    }

    private void startScan(){
        SCAN_TAG=getResources().getString(R.string.SCAN_STARTED);
        bluetoothLeScanner.startScan(leScanCallback);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                stopScan();
            }
        }, SCAN_PERIOD);
    }
    private void stopScan(){
        SCAN_TAG=getResources().getString(R.string.SCAN_STOPED);
        bluetoothLeScanner.stopScan(leScanCallback);
    }

    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                  if(passScanDeviceToActivity_interface!=null){
                      if(result!=null){
                          if((result.getDevice().getName()!=null)&&(result.getDevice().getName().length()>0)){
                              passScanDeviceToActivity_interface.sendCustomBleDevice(new CustBluetootDevices(result.getDevice().getAddress(),result.getDevice().getName(),result.getDevice(),false));
                          }else {
                              passScanDeviceToActivity_interface.sendCustomBleDevice(new CustBluetootDevices(result.getDevice().getAddress(),"NA",result.getDevice(),false));

                          }
                      }
                  }
                }
            };

    /**
     * Fragment Transaction
     */

    public void replaceFragmentTransaction(Fragment fragment,Bundle bundleData){
        fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    private void intializeFragmentManager(){
        fragmentManager=getSupportFragmentManager();
    }

    /**
     * Interface intialization (From Activity to Fragment)
     */
    public void  setupPassScanDeviceToActivity_interface(PassScanDeviceToActivity_interface loc_passScanDeviceToActivity_interface){
        this.passScanDeviceToActivity_interface=loc_passScanDeviceToActivity_interface;
    }



    public void setupPassConnectionStatusToFragment(PassConnectionStatusToFragment locpassConnectionStatusToFragment){
        this.passConnectionStatusToFragment=locpassConnectionStatusToFragment;
    }

    private void interfaceIntialization(){
        setupPassScanDeviceToActivity_interface(new PassScanDeviceToActivity_interface() {
            @Override
            public void sendCustomBleDevice(CustBluetootDevices custBluetootDevices) {

            }
        });

        setupPassConnectionStatusToFragment(new PassConnectionStatusToFragment() {
            @Override
            public void connectDisconnect(String bleAddress, boolean connected_disconnected) {

            }
        });
    }
    @Override
    public void makeDevieConnecteDisconnect(CustBluetootDevices custBluetootDevices, boolean connect_disconnect) {
        if(connect_disconnect){
            mBluetoothLeService.connect(custBluetootDevices.getBleAddress());
        }else {
            mBluetoothLeService.disconnect();
        }
    }

}