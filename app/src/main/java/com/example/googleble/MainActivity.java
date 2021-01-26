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
import android.content.AsyncQueryHandler;
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
import com.example.googleble.interfaceActivityFragment.DeviceConnectionTimeOut;
import com.example.googleble.interfaceActivityFragment.PassConnectionStatusToFragment;
import com.example.googleble.interfaceActivityFragment.PassScanDeviceToActivity_interface;
import com.example.googleble.interfaceActivityFragment.ShowDataForItemInRecycleView;
import com.example.googleble.interfaceFragmentActivity.DeviceConnectDisconnect;
import com.example.googleble.interfaceFragmentActivity.SendDataToBleDevice;
import static com.example.googleble.Utility.UtilityHelper.ble_on_off;

public class MainActivity extends AppCompatActivity
        implements
        DeviceConnectDisconnect,
        SendDataToBleDevice {
    /**
     * BluetoothLeService class Variables.
     */
    public BluetoothLeService mBluetoothLeService;
    private final static String TAG = MainActivity.class.getSimpleName();
    /**
     * Scan for the Ble Devices.
     */
    private BluetoothLeScanner bluetoothLeScanner =
            BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    private boolean mScanning;
    private Handler handler = new Handler();
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 30000;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    /**
     * Activity to Fragment interface
     */
    PassScanDeviceToActivity_interface passScanDeviceToActivity_interface;
    PassConnectionStatusToFragment passConnectionStatusToFragment;
    DeviceConnectionTimeOut deviceConnectionTimeOut;
    ShowDataForItemInRecycleView showDataForItemInRecycleView;
    public static String SCAN_TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        interfaceIntialization();
        bindBleServiceToMainActivity();
        intializeFragmentManager();
        replaceFragmentTransaction(new FragmentScan(), null);
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
        } else if (fragment.toString().equalsIgnoreCase(new FragmentData().toString())) {
            replaceFragmentTransaction(new FragmentScan(), null);
        }
    }

    /**
     * Code to manage Service life Cycle.
     */
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
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
    private final BroadcastReceiver bluetootServiceRecieverData = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS)))) {
                /**
                 * Connection/Disconnection of the Device.
                 */
                String bleAddress = intent.getStringExtra((getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS_BLE_ADDRESS)));
                boolean connectionStatus = intent.getBooleanExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS_CONNECTED_DISCONNECTED), false);
                passConnectionSucesstoFragmentScanForUIChange(bleAddress, connectionStatus);
            } else if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION)))) {
                /**
                 * Data Written to the firmware getting loop back after write confermation.
                 */
                String bleAddress = intent.getStringExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_BLE_ADDRESS));
                byte[] dataWritten = intent.getByteArrayExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_BLE_DATA_WRITTEN));
                int dataWrittenType = intent.getIntExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION_BLE_DATA_WRITTEN_TYPE), -1);
          }else if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED)))) {
                /**
                 * Data Obtained from the firmware.
                 */
                String bleAddress = intent.getStringExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED_BLE_ADDRESS));
                byte[] obtainedFromFirmware = intent.getByteArrayExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED_DATA_RECIEVED));
                if(showDataForItemInRecycleView!=null){
                    showDataForItemInRecycleView.recievedDataFromFirmware(bleAddress,obtainedFromFirmware);
                }
            }else if ((action != null) && (action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_TIMER_ACTION)))) {
                /**
                 * Logic to cacen the progress dialog and hide it.
                 * 1)show something went wrong try again later after some time...
                 * 2)Clear scan device and Scan again.
                 */
                boolean timerCancelled=intent.getBooleanExtra(getResources().getString(R.string.BLUETOOTHLE_SERVICE_TIMER_FINISH_KEY),false);
                passTimerOutConnectionTag(timerCancelled);
            }
        }

        private void passTimerOutConnectionTag(boolean result) {
            if(deviceConnectionTimeOut!=null){
                deviceConnectionTimeOut.connectionTimeOutTimer(result);
            }
        }

        private void passConnectionSucesstoFragmentScanForUIChange(String connectedDeviceAddress, boolean connect_disconnect) {
            if (passConnectionStatusToFragment != null) {
                passConnectionStatusToFragment.connectDisconnect(connectedDeviceAddress, connect_disconnect);
            }
        }
    };

    /**
     * BroadCast Reciever Data Trigger.
     */
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_CONNECTION_STATUS));
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_WRITTEN_FOR_CONFERMATION));
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_DATA_OBTAINED));
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_TIMER_ACTION));
        return intentFilter;
    }

    private void bindBleServiceToMainActivity() {
        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    public void start_stop_scan() {
        if(ble_on_off()){
            if (SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STOPED)) || (SCAN_TAG.equalsIgnoreCase(""))) {
                startScan();
            }else if (SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STARTED))) {
                /**
                 * Scan already started.
                 */
            }
        }
    }

    private void startScan() {
        SCAN_TAG = getResources().getString(R.string.SCAN_STARTED);
        bluetoothLeScanner.startScan(leScanCallback);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                stopScan();
            }
        }, SCAN_PERIOD);
    }

    private void stopScan() {
        SCAN_TAG = getResources().getString(R.string.SCAN_STOPED);
        bluetoothLeScanner.stopScan(leScanCallback);
    }

    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if (passScanDeviceToActivity_interface != null) {
                        if (result != null) {
                            if ((result.getDevice().getName() != null) && (result.getDevice().getName().length() > 0)) {
                                passScanDeviceToActivity_interface.sendCustomBleDevice(new CustBluetootDevices(result.getDevice().getAddress(), result.getDevice().getName(), result.getDevice(), false));
                            } else {
                                passScanDeviceToActivity_interface.sendCustomBleDevice(new CustBluetootDevices(result.getDevice().getAddress(), "NA", result.getDevice(), false));

                            }
                        }
                    }
                }
            };

    /**
     * Fragment Transaction
     */

    public void replaceFragmentTransaction(Fragment fragment, Bundle bundleData) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void intializeFragmentManager() {
        fragmentManager = getSupportFragmentManager();
    }

    /**
     * Interface intialization (From Activity to Fragment)
     */
    public void setupPassScanDeviceToActivity_interface(PassScanDeviceToActivity_interface loc_passScanDeviceToActivity_interface) {
        this.passScanDeviceToActivity_interface = loc_passScanDeviceToActivity_interface;
    }


    public void setupPassConnectionStatusToFragment(PassConnectionStatusToFragment locpassConnectionStatusToFragment) {
        this.passConnectionStatusToFragment = locpassConnectionStatusToFragment;
    }

    public void setUpDeviceConnectionTimeOut(DeviceConnectionTimeOut deviceConnectionTimeOut_loc){
        this.deviceConnectionTimeOut=deviceConnectionTimeOut_loc;
    }

    public void setUpShowDataForItemInRecycleView(ShowDataForItemInRecycleView loc_showDataForItemInRecycleView){
        this.showDataForItemInRecycleView=loc_showDataForItemInRecycleView;
    }

    private void interfaceIntialization() {
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

        setUpDeviceConnectionTimeOut(new DeviceConnectionTimeOut() {
            @Override
            public void connectionTimeOutTimer(boolean result) {

            }
        });

        setUpShowDataForItemInRecycleView(new ShowDataForItemInRecycleView() {
            @Override
            public void recievedDataFromFirmware(String bleAddress,byte[] dataRecievedFromFirmware) {

            }
        });
    }

    @Override
    public void makeDevieConnecteDisconnect(CustBluetootDevices custBluetootDevices, boolean connect_disconnect) {
        if (connect_disconnect) {
            boolean connectissue = mBluetoothLeService.connect(custBluetootDevices.getBleAddress());
        } else {
            mBluetoothLeService.disconnect(custBluetootDevices.getBleAddress());

        }
    }

    @Override
    public void parseDataToBleDevice(CustBluetootDevices custBluetootDevices, byte[] dataToSend) {
        mBluetoothLeService.sendDataToBleDevice(custBluetootDevices.getBleAddress(), dataToSend);
    }
}