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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.googleble.CustomObjects.CustBluetootDevices;
import com.example.googleble.Fragment.FragmentScan;
import com.example.googleble.Service.BluetoothLeService;
import com.example.googleble.interfaceActivityFragment.PassScanDeviceToActivity_interface;
import com.example.googleble.interfaceFragmentActivity.DeviceClikckedForConnection;

public class MainActivity extends AppCompatActivity implements DeviceClikckedForConnection {
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
        scanLeDevice();
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
                System.out.println("MainActivity Device Connected ");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                System.out.println("MainActivity Device Service Discovered ");

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data=intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                System.out.println("MainActivity Device Data avaliable=  "+data);
            }else if((action!=null)&&(action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_BLE_ADDRESS)))){


            }
        }
    };
    /**
     * BroadCast Reciever Data Trigger.
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
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

    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                  if(passScanDeviceToActivity_interface!=null){
                      if(result!=null&&result.getDevice().getName()!=null){
                          passScanDeviceToActivity_interface.sendCustomBleDevice(new CustBluetootDevices(result.getDevice().getAddress(),result.getDevice().getName(),result.getDevice(),false));
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

    private void interfaceIntialization(){
        setupPassScanDeviceToActivity_interface(new PassScanDeviceToActivity_interface() {
            @Override
            public void sendCustomBleDevice(CustBluetootDevices custBluetootDevices) {

            }
        });
    }

    @Override
    public void connectToDevice(CustBluetootDevices custBluetootDevices) {
        mBluetoothLeService.connect(custBluetootDevices.getBleAddress());
    }
}