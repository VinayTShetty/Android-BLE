package com.example.googleble.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.googleble.BaseFragment.BaseFragment;
import com.example.googleble.MainActivity;
import com.example.googleble.R;

public class FragmentData extends BaseFragment {
    View fragmentDataView;
    MainActivity myMainActivity;
    Button dfuUpdate,selectfile;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myMainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentDataView = inflater.inflate(R.layout.fragment_data, container, false);
        dfuUpdate=(Button)fragmentDataView.findViewById(R.id.dfu_update);
        selectfile=(Button)fragmentDataView.findViewById(R.id.select_file);
        buttonCliclistner();
        return fragmentDataView;
    }

    private void buttonCliclistner(){
        dfuUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Hello world");
            }
        });
        selectfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        return FragmentData.class.getSimpleName();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final Uri uri = data.getData();
        System.out.println("uri "+uri);
    }
}
