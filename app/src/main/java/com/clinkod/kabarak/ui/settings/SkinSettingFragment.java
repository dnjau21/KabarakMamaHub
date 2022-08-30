package com.clinkod.kabarak.ui.settings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.clinkod.kabarak.R;
import com.clinkod.kabarak.models.SkinSetting;
import com.clinkod.kabarak.services.DataReadService;
import com.clinkod.kabarak.ui.MainActivityFragment;
import com.yucheng.ycbtsdk.Response.BleDataResponse;
import com.yucheng.ycbtsdk.YCBTClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SkinSettingFragment extends MainActivityFragment {

    private GridView listView;
    private SkinAdapter skinAdapter;
    private ArrayList<String> frequencyOptions;
    private Button btnSave;
    private NavController controller;
    private Spinner frequencyInput;
    private static String TAG = SkinSettingFragment.class.getSimpleName();
    private ProgressDialog progressD;
    public static SkinSettingFragment newInstance() {
        return new SkinSettingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.skin_setting_fragment, container, false);
        controller = NavHostFragment.findNavController(this);
        listView = root.findViewById(R.id.colorList);
        skinAdapter = new SkinAdapter(getActivity());
        progressD = new ProgressDialog(getActivity());
        btnSave = root.findViewById(R.id.btnSave);



        listView.setAdapter(skinAdapter);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            SkinSetting.SkinColor color = skinAdapter.getItem(i);

            SkinSetting.setSkinColor(color.getValue());
            skinAdapter.setSelectedColor(color.getValue());

            skinAdapter.notifyDataSetChanged();
        });
        btnSave.setOnClickListener(v -> {
            setSkinColor(skinAdapter.getSelectedColor());
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       /* DataReadService.getInstance().disconnect();
        getActivity().stopService(new Intent(getContext(), DataReadService.class));
        DataReadService.startActionDataReadService(getActivity());*/
    }
    private void showDialogMessage(String title, String message){
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Close", (dialog, which) -> {
                    dialog.dismiss();
                }).show();
    }

    private void setSkinColor(int color) {
        setSkinColor(color, true);
    }

    private void setSkinColor(int color, boolean retry) {
        progressD.setMessage("Setting Skin tone...");
        progressD.show();
        SkinSetting.setSkinColor(color);
        AtomicBoolean commandCompleted = new AtomicBoolean(false);


        YCBTClient.settingSkin(SkinSetting.getSkinColor(), new BleDataResponse() {

            public final void onDataResponse(int i, float f, HashMap hashMap) {
                updateSkinColor(commandCompleted, i, f, hashMap);
            }
        });
        new Handler().postDelayed(new Runnable() {
            public final void run() {
                updateWactch(commandCompleted, retry, color);
            }
        }, 3000);

//       YCBTClient.settingSkin(SkinSetting.getSkinColor(), (i, v, hashMap) -> {
//            // Log.d(TAG, "Skin Color set: " + SkinSetting.getSkinColor());
//           commandCompleted.set(true);
//           if(i == Constants.CODE.Code_OK){
//               // Log.d(TAG, "Skin Color setting successful");
//
//               getActivity().runOnUiThread(()-> {
//                   Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
//                   controller.popBackStack();
//               });
//           }else{
//               // Log.d(TAG, "Skin Color setting failed");
//
//               getActivity().runOnUiThread(()-> {
//                   Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
//               });
//           }
//       });
//
//        new Handler().postDelayed(()-> {
//            if(commandCompleted.get()){
//                // Do nothing
//            }else if (retry){
//
//                DataReadService.getInstance().disconnect();
//                getActivity().stopService(new Intent(getContext(), DataReadService.class));
//                DataReadService.startActionDataReadService(getActivity());
//
//                new Handler().postDelayed(()-> setSkinColor(color, false), 5000); // Wait for the watch to reconnect
//            } else {
//                getActivity().runOnUiThread(()-> showDialogMessage("Error",
//                        "Kindly restart your watch"));
//            }
//        }, 3000);
    }

    public  void updateSkinColor(AtomicBoolean commandCompleted, int i, float v, HashMap hashMap) {
        String str = TAG;
        // Log.d(str, "Skin Color set: " + SkinSetting.getSkinColor());
        // Log.d(str, "Skin i : " + i);
        progressD.cancel();
        commandCompleted.set(true);
        if (i == 0) {
            // Log.d(TAG, "Skin Color setting successful");
            getActivity().runOnUiThread(new Runnable() {
                public final void run() {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }else {
            getActivity().runOnUiThread(new Runnable() {
                public final void run() {
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    public  void updateWactch(AtomicBoolean commandCompleted, boolean retry, int color) {
        if (!commandCompleted.get()) {
            if (retry) {
                DataReadService.getInstance().disconnect();
                getActivity().stopService(new Intent(getContext(), DataReadService.class));
                DataReadService.startActionDataReadService(getActivity());
                new Handler().postDelayed(new Runnable() {


                    public final void run() {
                        setSkinColor(color, false);
                    }
                }, 5000);
                return;
            }
            progressD.cancel();
            getActivity().runOnUiThread(new Runnable() {
                public final void run() {
                    showDialogMessage("Error", "Please turn off the watch, then turn it back on and try again!");
                }
            });
        }
    }
}