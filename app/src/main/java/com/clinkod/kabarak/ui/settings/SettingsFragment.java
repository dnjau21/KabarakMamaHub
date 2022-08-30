package com.clinkod.kabarak.ui.settings;

import android.widget.*;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clinkod.kabarak.LoginActivity;
import com.clinkod.kabarak.MainActivity;
import com.clinkod.kabarak.Registration;
import com.clinkod.kabarak.R;
import com.clinkod.kabarak.models.BioData;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.ui.MainActivityFragment;
import com.clinkod.kabarak.ui.OtpActivity;
import com.clinkod.kabarak.ui.bioinfo.BioInfoFragment;
import com.clinkod.kabarak.ui.devicescan.DeviceScanActivity;
import com.clinkod.kabarak.ui.onboarding.OnBoarding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsFragment extends MainActivityFragment {

    private SettingsViewModel mViewModel;
    private TextView userName, logout;
    private BioData bioData;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private static final String HAS_ONBOARDED = "has_completed_onboarding";
    private ImageView userProfile;
    private static final String FIRST_TIME_USE = "first_time_use";

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        NavController controller = NavHostFragment.findNavController(this);

        View view = inflater.inflate(R.layout.settings_fragment, container, false);


        userName = view.findViewById(R.id.name);
        userProfile = view.findViewById(R.id.userProfile);
        logout = view.findViewById(R.id.logout);


        goToUserProfile();
        userLogout();

        try {
            bioData = BioData.getBioData();
            userName.setText(bioData.getFirstName());
        }catch (Exception e){
            userName.setText("User");
        }

//        TODO: Have setting for the user to define the time intervals

        ArrayList<String> settingItems = new ArrayList<String>();
        settingItems.addAll(Arrays.asList("Connect to Watch", "Skin Setting","Periodic Setting","My Profile","Next of Kin", "About Us", "Logout"));

        ArrayAdapter<String> listAdapter =
                new ArrayAdapter<>(getContext(),
                        R.layout.settings_item_layout, R.id.rowTextView, settingItems);

        ListView settingListView = view.findViewById(R.id.settings_list);

        settingListView.setAdapter(listAdapter);

        settingListView.setOnItemClickListener((parent, view1, position, id) -> {
            switch (position) {
                case 0:
//                    controller.navigate(SettingsFragmentDirections.actionNavigationSettingsToDeviceScanFragment2());
                    startActivity(new Intent(getActivity(), DeviceScanActivity.class));
                    break;
                case 1:
                    controller.navigate(SettingsFragmentDirections.actionNavigationSettingsToSkinSettingFragment());
                    break;
                case 2:
                    controller.navigate(SettingsFragmentDirections.actionNavigationSettingsToPeriodicFragment());
                    break;
                case 3:
                    startActivity(new Intent(getActivity(), Registration.class));
//                    controller.navigate(SettingsFragmentDirections.actionNavigationSettingsToProfileFragment());
                    break;
                case 4:
                    controller.navigate(SettingsFragmentDirections.actionNavigationSettingsToNextOfKinFragment());
                    break;
                case 5:
                    controller.navigate(SettingsFragmentDirections.actionNavigationSettingsToAboutUsFragment());
                    break;
                case 6:
                    PropertyUtils.setHasCompletedOnboarding(false);
                    PropertyUtils.logIn(false);
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
            }
        });
        return view;
    }

    private void userLogout() {
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PropertyUtils.logIn(false);
                Intent intent = new Intent(getActivity(), OnBoarding.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
    }

    private void goToUserProfile() {

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment userProfile= new BioInfoFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.nav_host_fragment, userProfile);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
    }

    private void gotoOnBoarding() {
        Intent intent =new Intent(getActivity(), Registration.class);
        startActivity(intent);

    }

    private void checkData() {
        if (bioData !=null && bioData.getFirstName()!=null){
            Toast.makeText(getContext(), "We have no data", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getContext(), "We have no data", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        // TODO: Use the ViewModel
    }
}