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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googleble.Adapter.FragmentScanAdapter;
import com.example.googleble.BaseFragment.BaseFragment;
import com.example.googleble.CustomObjects.CustBluetootDevices;
import com.example.googleble.MainActivity;
import com.example.googleble.R;
import com.example.googleble.interfaceActivityFragment.PassConnectionStatusToFragment;
import com.example.googleble.interfaceActivityFragment.PassScanDeviceToActivity_interface;
import com.example.googleble.interfaceFragmentActivity.DeviceConnectDisconnect;

import java.util.ArrayList;
import java.util.List;

import static com.example.googleble.Utility.UtilityHelper.showPermissionDialog;

public class FragmentScan extends BaseFragment {
    View fragmenScanView;
    private final int LocationPermissionRequestCode = 100;
    MainActivity myMainActivity;
    FragmentScanAdapter my_fragmentScanAdapter;
    RecyclerView fragmentScanRecycleView;
    private ArrayList<CustBluetootDevices> custBluetootDevicesArrayList = new ArrayList<CustBluetootDevices>();
    DeviceConnectDisconnect deviceConnectDisconnect;

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
        setHasOptionsMenu(true);
        interfaceImplementationCallBack();
        setUpRecycleView();
        fragmentSscaAdapterInterfaceImplementation();
        return fragmenScanView;
    }

    private void fragmentSscaAdapterInterfaceImplementation() {
        my_fragmentScanAdapter.setOnItemClickLIstner(new FragmentScanAdapter.ScanOnItemClickInterface() {
            @Override
            public void ClickedItem(CustBluetootDevices custBluetootDevices, int positionClicked) {
                if (deviceConnectDisconnect != null) {
                    if (custBluetootDevices.isConnected()) {
                        deviceConnectDisconnect.makeDevieConnecteDisconnect(custBluetootDevices, false);
                    } else if (!custBluetootDevices.isConnected()) {
                        deviceConnectDisconnect.makeDevieConnecteDisconnect(custBluetootDevices, true);
                    }
                }
            }

        });
    }

    private void interfaceIntialization() {
        deviceConnectDisconnect = (DeviceConnectDisconnect) getActivity();
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
                Toast.makeText(getActivity(), "Stop", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.scan_item:
                Toast.makeText(getActivity(), "Scan", Toast.LENGTH_SHORT).show();
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
                    myMainActivity.scanLeDevice();
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
                        myMainActivity.replaceFragmentTransaction(new FragmentData(),null);
                    }
                } else {
                    CustBluetootDevices custBluetootDevices = new CustBluetootDevices();
                    custBluetootDevices.setBleAddress(bleAddress);
                    if (custBluetootDevicesArrayList.contains(custBluetootDevices)) {
                        int postion = custBluetootDevicesArrayList.indexOf(custBluetootDevices);
                        CustBluetootDevices custBluetootDevices1 = custBluetootDevicesArrayList.get(postion);
                        custBluetootDevices1.setConnected(false);
                        my_fragmentScanAdapter.notifyItemChanged(postion);
                    }
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
        List<BluetoothDevice> connectedDevicesList = myMainActivity.mBluetoothLeService.getListOfConnectedDevices();
        for (BluetoothDevice bluetoothDevice : connectedDevicesList) {
            CustBluetootDevices custBluetootDevices = new CustBluetootDevices();
            custBluetootDevices.setBleAddress(bluetoothDevice.getAddress());
            if (bluetoothDevice.getName() != null) {
                custBluetootDevices.setDeviceName(bluetoothDevice.getName());
            } else {
                custBluetootDevices.setDeviceName("NA");
            }
        }
    }

}
