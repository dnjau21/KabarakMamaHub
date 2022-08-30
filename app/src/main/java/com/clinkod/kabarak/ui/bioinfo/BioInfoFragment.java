package com.clinkod.kabarak.ui.bioinfo;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clinkod.kabarak.R;
import com.clinkod.kabarak.models.BioData;

public class BioInfoFragment extends Fragment {

    private BioInfoViewModel mViewModel;
    private TextView name, dob, gender, height, weight, hypertensionHistory, phone;
    private BioData bioData;
    private  ImageView imgSettings;
    NavController controller;


    public static BioInfoFragment newInstance() {
        return new BioInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bio_info_fragment, container, false);

        NavController controller = NavHostFragment.findNavController(this);

        name = view.findViewById(R.id.name);
        dob = view.findViewById(R.id.age);
        gender = view.findViewById(R.id.gender);
        height = view.findViewById(R.id.height);
        weight = view.findViewById(R.id.weight);
        hypertensionHistory = view.findViewById(R.id.hypertensionHistory);
        phone = view.findViewById(R.id.phone);


        bioData = BioData.getBioData();

        name.setText(bioData.getFirstName());
        dob.setText(bioData.getDateOfBirth());
        gender.setText(bioData.getMarital_status());
        height.setText(String.valueOf(bioData.getHeight()));
        weight.setText(String.valueOf(bioData.getWeight()));
        hypertensionHistory.setText(bioData.isFamilyHypertensionHistory() ? "Yes": "No");
        phone.setText(bioData.getPhone());

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BioInfoViewModel.class);
        // TODO: Use the ViewModel
    }

}