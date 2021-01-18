package com.example.googleble.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.example.googleble.interfaceActivityFragment.PassScanDeviceToActivity_interface;

import java.util.ArrayList;

import static com.example.googleble.Utility.UtilityHelper.showPermissionDialog;

public class FragmentScan extends BaseFragment {
    View fragmenScanView;
    private final int LocationPermissionRequestCode = 100;
    MainActivity myMainActivity;
    FragmentScanAdapter my_fragmentScanAdapter;
    RecyclerView fragmentScanRecycleView;
    private ArrayList<CustBluetootDevices> custBluetootDevicesArrayList=new ArrayList<CustBluetootDevices>();
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myMainActivity=(MainActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmenScanView = inflater.inflate(R.layout.fragment_scan, container, false);
        intializeView();
        interfaceImplementationCallBack();
        setUpRecycleView();
        return  fragmenScanView;
    }


    private void intializeView() {
        fragmentScanRecycleView=fragmenScanView.findViewById(R.id.fragment_scan_recycleView);
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
            Toast.makeText(getActivity(),"Location Permission Not Given",Toast.LENGTH_SHORT).show();
        } else {
            showPermissionDialog(getActivity());
        }
    }

    private void interfaceImplementationCallBack() {
        myMainActivity.setupPassScanDeviceToActivity_interface(new PassScanDeviceToActivity_interface() {
            @Override
            public void sendCustomBleDevice(CustBluetootDevices custBluetootDevices) {

                if(!custBluetootDevicesArrayList.contains(custBluetootDevices)){
                    custBluetootDevicesArrayList.add(custBluetootDevices);
                    my_fragmentScanAdapter.notifyDataSetChanged();
                };
            }
        });
    }

    private void setUpRecycleView(){
        my_fragmentScanAdapter=new FragmentScanAdapter(custBluetootDevicesArrayList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        fragmentScanRecycleView.setLayoutManager(mLayoutManager);
        fragmentScanRecycleView.setAdapter(my_fragmentScanAdapter);
    }

}
