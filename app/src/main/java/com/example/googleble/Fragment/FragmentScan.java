package com.example.googleble.Fragment;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.googleble.Adapter.FragmentScanAdapter;
import com.example.googleble.BaseFragment.BaseFragment;
import com.example.googleble.CustomObjects.CustBluetootDevices;
import com.example.googleble.DialogHelper.ShowDialogHelper;
import com.example.googleble.MainActivity;
import com.example.googleble.R;
import com.example.googleble.interfaceActivityFragment.DeviceConnectionTimeOut;
import com.example.googleble.interfaceActivityFragment.PassConnectionStatusToFragment;
import com.example.googleble.interfaceActivityFragment.PassScanDeviceToActivity_interface;
import com.example.googleble.interfaceFragmentActivity.DeviceConnectDisconnect;
import com.example.googleble.interfaceFragmentActivity.SendDataToBleDevice;
import com.kaopiz.kprogresshud.KProgressHUD;
import java.util.ArrayList;
import java.util.List;
import static com.example.googleble.Utility.UtilityHelper.ble_on_off;
import static com.example.googleble.Utility.UtilityHelper.showPermissionDialog;

public class FragmentScan extends BaseFragment {
    View fragmenScanView;
    private final int LocationPermissionRequestCode = 100;
    MainActivity myMainActivity;
    FragmentScanAdapter my_fragmentScanAdapter;
    RecyclerView fragmentScanRecycleView;
    private ArrayList<CustBluetootDevices> custBluetootDevicesArrayList = new ArrayList<CustBluetootDevices>();
    /**
     * interface intialization from FragmentToActivity
     */
    DeviceConnectDisconnect deviceConnectDisconnect;
    SendDataToBleDevice sendDataToBleDevice;
    ShowDialogHelper showDialogHelper;
    KProgressHUD progressDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myMainActivity = (MainActivity) getActivity();
        interfaceIntialization();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmenScanView = inflater.inflate(R.layout.fragment_scan, container, false);
        intializeView();
        intializeDailogHelper();
        setHasOptionsMenu(true);
        interfaceImplementationCallBack();
        setUpRecycleView();
        fragmentSscaAdapterInterfaceImplementation();
        getListOfConnectedDevices();
        checkPermissionGiven();
        return fragmenScanView;
    }

    private void intializeDailogHelper() {
        showDialogHelper=new ShowDialogHelper(getActivity());
    }

    private void fragmentSscaAdapterInterfaceImplementation() {
        my_fragmentScanAdapter.setOnItemClickLIstner(new FragmentScanAdapter.ScanOnItemClickInterface() {
            @Override
            public void ClickedItem(CustBluetootDevices custBluetootDevices, int positionClicked) {
                if (deviceConnectDisconnect != null) {
                    if (custBluetootDevices.isConnected()) {
                        showProgressDialog(custBluetootDevices.getBleAddress(),"Disonnecting ");
                        deviceConnectDisconnect.makeDevieConnecteDisconnect(custBluetootDevices, false);
                        System.out.println("CONNECT_DISCONNECT disconnectionFragmentScan");
                    } else if (!custBluetootDevices.isConnected()) {
                        if(ble_on_off()){
                            showProgressDialog(custBluetootDevices.getBleAddress(),"Connectiong ");
                            deviceConnectDisconnect.makeDevieConnecteDisconnect(custBluetootDevices, true);
                        }else {
                            showDialogHelper.errorDialog("Turn on Bluetooth");
                        }
                    }
                }
            }

            @Override
            public void sendDataButtonClickedForItem(CustBluetootDevices custBluetootDevices, int positionClicked) {
                                if(sendDataToBleDevice!=null){
                                    sendDataToBleDevice.parseDataToBleDevice(custBluetootDevices,"SMPON".getBytes());
                                }
            }

            @Override
            public void resetTextViewDataOnClickOfTextView(CustBluetootDevices custBluetootDevices, int positionClicked) {
                custBluetootDevices.setDataObtained("");
                my_fragmentScanAdapter.notifyItemChanged(positionClicked);
            }
        });
    }

    private void interfaceIntialization() {
        deviceConnectDisconnect = (DeviceConnectDisconnect) getActivity();
        sendDataToBleDevice=(SendDataToBleDevice)getActivity();
        progressDialog=KProgressHUD.create(getActivity());
    }
    private void showProgressDialog(String bleAddress,String detailedLabel){
        progressDialog.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setLabel("Please wait")
                        .setDetailsLabel(detailedLabel+" "+bleAddress)
                        .setCancellable(false)
                        .show();
    }
    private void cancelProgressDialog(){
        if(progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }


    private void intializeView() {
        fragmentScanRecycleView = fragmenScanView.findViewById(R.id.fragment_scan_recycleView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public String toString() {
        return FragmentScan.class.getSimpleName();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_scan_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stop_item:
                return true;
            case R.id.scan_item:
                clearScannedDevices();
                getListOfConnectedDevices();
                myMainActivity.start_stop_scan();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LocationPermissionRequestCode:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myMainActivity.start_stop_scan();
                } else {
                    askPermission();
                }
        }
    }

    private void askPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(getActivity(), "Location Permission Not Given", Toast.LENGTH_SHORT).show();
        } else {
            showPermissionDialog(getActivity());
        }
    }

    private void interfaceImplementationCallBack() {
        myMainActivity.setupPassScanDeviceToActivity_interface(new PassScanDeviceToActivity_interface() {
            @Override
            public void sendCustomBleDevice(CustBluetootDevices custBluetootDevices) {
                if (!custBluetootDevicesArrayList.contains(custBluetootDevices)) {
                    custBluetootDevicesArrayList.add(custBluetootDevices);
                    my_fragmentScanAdapter.notifyDataSetChanged();
                }
                ;
            }
        });

        myMainActivity.setupPassConnectionStatusToFragment(new PassConnectionStatusToFragment() {
            @Override
            public void connectDisconnect(String bleAddress, boolean connected_disconnected) {
                if (connected_disconnected) {
                    CustBluetootDevices custBluetootDevices = new CustBluetootDevices();
                    custBluetootDevices.setBleAddress(bleAddress);
                    if (custBluetootDevicesArrayList.contains(custBluetootDevices)) {
                        int postion = custBluetootDevicesArrayList.indexOf(custBluetootDevices);
                        CustBluetootDevices custBluetootDevices1 = custBluetootDevicesArrayList.get(postion);
                        custBluetootDevices1.setConnected(true);
                        my_fragmentScanAdapter.notifyItemChanged(postion);
                        cancelProgressDialog();
                    }
                } else {
                    CustBluetootDevices custBluetootDevices = new CustBluetootDevices();
                    custBluetootDevices.setBleAddress(bleAddress);
                    if (custBluetootDevicesArrayList.contains(custBluetootDevices)) {
                        int postion = custBluetootDevicesArrayList.indexOf(custBluetootDevices);
                        CustBluetootDevices custBluetootDevices1 = custBluetootDevicesArrayList.get(postion);
                        custBluetootDevices1.setConnected(false);
                        my_fragmentScanAdapter.notifyItemChanged(postion);
                        cancelProgressDialog();
                    }
                }
            }
        });

        myMainActivity.setUpDeviceConnectionTimeOut(new DeviceConnectionTimeOut() {
            @Override
            public void connectionTimeOutTimer(boolean result) {
                if(result){
                    cancelProgressDialog();
                    custBluetootDevicesArrayList.clear();
                    getListOfConnectedDevices();
                    myMainActivity.start_stop_scan();
                }
            }
        });

    }

    private void setUpRecycleView() {
        my_fragmentScanAdapter = new FragmentScanAdapter(custBluetootDevicesArrayList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        fragmentScanRecycleView.setLayoutManager(mLayoutManager);
        fragmentScanRecycleView.setAdapter(my_fragmentScanAdapter);
    }

    private void getListOfConnectedDevices() {
        if(ble_on_off()){
            if(myMainActivity.mBluetoothLeService!=null){
                List<BluetoothDevice> connectedDevicesList = myMainActivity.mBluetoothLeService.getListOfConnectedDevices();
                if((connectedDevicesList!=null)&&(connectedDevicesList.size()>0)){
                    for (BluetoothDevice bluetoothDevice : connectedDevicesList) {
                        CustBluetootDevices custBluetootDevices = new CustBluetootDevices();
                        custBluetootDevices.setBleAddress(bluetoothDevice.getAddress());
                        custBluetootDevices.setConnected(true);
                        if (bluetoothDevice.getName() != null) {
                            custBluetootDevices.setDeviceName(bluetoothDevice.getName());
                        } else {
                            custBluetootDevices.setDeviceName("NA");
                        }
                        custBluetootDevicesArrayList.add(custBluetootDevices);
                        my_fragmentScanAdapter.notifyDataSetChanged();
                    }
                }
            }
        }else {
            custBluetootDevicesArrayList.clear();
            my_fragmentScanAdapter.notifyDataSetChanged();
        }
    }
    private void clearScannedDevices(){
        custBluetootDevicesArrayList.clear();
        my_fragmentScanAdapter.notifyDataSetChanged();
    }

    private void checkPermissionGiven() {
        if (isAdded()) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        myMainActivity.start_stop_scan();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LocationPermissionRequestCode);
            }
        }else {
            System.out.println("SCAN NOT VISIBLE");
        }
    }

    private void sendDataToBleDevice(){

    }
}
