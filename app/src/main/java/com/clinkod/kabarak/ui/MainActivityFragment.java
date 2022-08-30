package com.clinkod.kabarak.ui;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.clinkod.kabarak.MainActivity;

public class MainActivityFragment extends Fragment {
    public Activity  mActivity;
    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        activity.checkBluetoothStatus();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }
}
