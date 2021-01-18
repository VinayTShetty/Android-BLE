package com.example.googleble.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googleble.CustomObjects.CustBluetootDevices;
import com.example.googleble.R;

import java.util.ArrayList;

public class FragmentScanAdapter extends RecyclerView.Adapter<FragmentScanAdapter.ScanItemViewHolder> {
    private ArrayList<CustBluetootDevices> customBluetoothdevices;
    private Context context;

    public FragmentScanAdapter(ArrayList<CustBluetootDevices> loc_custBluetootDevicesArrayList){
        customBluetoothdevices=loc_custBluetootDevicesArrayList;
    }

    @NonNull
    @Override
    public ScanItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.fragment_scan_item_layout, parent, false);
        return new FragmentScanAdapter.ScanItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanItemViewHolder scanItemViewHolder, int position) {
        scanItemViewHolder.bindBluetoothDeviceDetails(customBluetoothdevices.get(position),scanItemViewHolder);
    }

    @Override
    public int getItemCount() {
        return customBluetoothdevices.size();
    }

    public class ScanItemViewHolder  extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView bleAddress_textView;
        Button connectButton_button;
        public ScanItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.bleAddress_textView=(TextView)itemView.findViewById(R.id.ble_address);
            this.connectButton_button=(Button)itemView.findViewById(R.id.connect_button);
        }

        @Override
        public void onClick(View itemView) {
                itemView.setOnClickListener(this);
        }

        void bindBluetoothDeviceDetails(CustBluetootDevices custBluetootDevices,ScanItemViewHolder scanItemViewHolder){
            bleAddress_textView.setText(custBluetootDevices.getBleAddress());
            if(custBluetootDevices.isConnected()){
                connectButton_button.setText("DisConnect");
            }else if(!(custBluetootDevices.isConnected())){
                connectButton_button.setText("Connecet");
            }
        }
    }
}
