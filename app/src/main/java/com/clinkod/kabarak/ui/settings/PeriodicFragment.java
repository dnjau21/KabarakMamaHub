package com.clinkod.kabarak.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.PowerManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.clinkod.kabarak.R;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.ui.radiobutton.CustomRadioGroup;
import com.clinkod.kabarak.ui.radiobutton.OnCustomRadioButtonListener;
import com.clinkod.kabarak.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PeriodicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeriodicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button btnSave;
    int frequency=0;
    int viewIdSelected=0;
    public static final int Manual_mode = 0x00;
    public static final int Auto_mode =  0x01;
    FrameLayout framelayour;
    private static String TAG = PeriodicFragment.class.getSimpleName();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PeriodicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PeriodicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PeriodicFragment newInstance(String param1, String param2) {
        PeriodicFragment fragment = new PeriodicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_periodic, container, false);
        initOnCustomRadioGroupListener();
        NavController controller = NavHostFragment.findNavController(this);

        btnSave = view.findViewById(R.id.btnSave);
        framelayour = view.findViewById(R.id.framelayour);
        btnSave.setOnClickListener(v -> {
            PropertyUtils.setMeasureFrequency(frequency);
            PropertyUtils.setSelectedView(viewIdSelected);
            Utils.setUpAlarms(getContext());
            Snackbar snackbar = Snackbar.make(framelayour, "Frequency set successfully", Snackbar.LENGTH_SHORT);
            snackbar.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
            snackbar.setBackgroundTint(ContextCompat.getColor(getContext(),R.color.inactive_bottom_nav));
            snackbar.show();
            NavHostFragment.findNavController(this).popBackStack();

            //BleUtils.settingRestoreFactory();
            //BleUtils.syncBPData(getActivity());
//            BleUtils.getDeviceConfig();
//            BleUtils.getDeviceLog();
//            BleUtils.getScheduleInfo();

        });

        PowerManager powerManager = (PowerManager)
                getContext().getSystemService(Context.POWER_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && powerManager.isPowerSaveMode())
        {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Power saving mode ")
                    .setMessage("You have turned on power saving mode on your device. Please turn it off to use this feature")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //set what would happen when positive button is clicked
                            NavHostFragment.findNavController(getParentFragment()).popBackStack();
                        }
                    })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            //set what should happen when negative button is clicked
//                            Toast.makeText(getContext(),"Nothing Happened",Toast.LENGTH_LONG).show();
//                        }
//                    })
                    .show();
        }

        return view;
    }

    private void initOnCustomRadioGroupListener() {
        CustomRadioGroup.setOnClickListener((OnCustomRadioButtonListener) view -> {
            switch (view.getId()) {
                case R.id.fivemin:
                    showButtonTag(R.id.fivemin);
                    break;
                case R.id.fifteenmin:
                    showButtonTag(R.id.fifteenmin);
                    break;
                case R.id.thirtymin:
                    showButtonTag(R.id.thirtymin);
                    break;
                case R.id.fourtyfivemin:
                    showButtonTag(R.id.fourtyfivemin);
                case R.id.onehr:
                    showButtonTag(R.id.onehr);
                    break;
                case R.id.twohr:
                    showButtonTag(R.id.twohr);
                    break;
                case R.id.threehr:
                    showButtonTag(R.id.threehr);
                    break;
                case R.id.fourhr:
                    showButtonTag(R.id.fourhr);
                case R.id.fivehr:
                    showButtonTag(R.id.fivehr);
                    break;
                case R.id.sixhr:
                    showButtonTag(R.id.sixhr);
                    break;

                default:
                    showButtonTag(-1);
                    break;
            }
        });
    }

    private void showButtonTag(int viewId) {
        PeriodicRadioButtonMapper mapper = new PeriodicRadioButtonMapper(getActivity());

        frequency = Integer.parseInt(mapper.mapToStrigFrom(viewId));
        viewIdSelected = viewId;

    }
}