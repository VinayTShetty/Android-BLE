package com.example.googleble.Adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentScanAdapter extends RecyclerView.Adapter<FragmentScanAdapter.ScanItemViewHolder> {
    @NonNull
    @Override
    public ScanItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ScanItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ScanItemViewHolder  extends  RecyclerView.ViewHolder implements View.OnClickListener{

        public ScanItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View itemView) {
                itemView.setOnClickListener(this);
        }
    }
}
