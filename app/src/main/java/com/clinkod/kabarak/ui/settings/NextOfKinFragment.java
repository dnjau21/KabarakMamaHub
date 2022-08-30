package com.clinkod.kabarak.ui.settings;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.clinkod.kabarak.R;
import com.clinkod.kabarak.models.NextOfKin;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.preference.PrefManager;
import com.clinkod.kabarak.services.DataSynchronizationService;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NextOfKinFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NextOfKinFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextInputEditText ted_first_name, ted_last_name, ted_id_number, ted_relationship, tedPhone;
    String first_name, last_name, relationship, phone;
    int id_number;
    Button btn;
    ConstraintLayout constraintLayout;
    boolean isAllFieldsChecked = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NextOfKinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NextOfKinFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NextOfKinFragment newInstance(String param1, String param2) {
        NextOfKinFragment fragment = new NextOfKinFragment();
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
        View view = inflater.inflate(R.layout.fragment_next_of_kin, container, false);

        ted_first_name = view.findViewById(R.id.ted_first_name);
        ted_last_name = view.findViewById(R.id.ted_last_name);
        ted_id_number = view.findViewById(R.id.ted_id_number);
        ted_relationship = view.findViewById(R.id.ted_relationship);
        tedPhone = view.findViewById(R.id.tedPhone);

        first_name = PrefManager.getNextOfKinFirstName(getContext());
        last_name = PrefManager.getNextOfKinLastname(getContext());
        id_number = PrefManager.getNextOfKinIdNumber(getContext());
        relationship = PrefManager.getNextOfKinRelationship(getContext());
        phone = PrefManager.getNextOfKinPhoneNumber(getContext());

        if(PropertyUtils.hasHasNextOfKin()){

            ted_first_name.setText(first_name);
            ted_last_name.setText(last_name);
            ted_id_number.setText(String.valueOf(id_number));
            ted_relationship.setText(relationship);
            tedPhone.setText(phone);
        }


        btn = view.findViewById(R.id.btnSave);




        btn.setOnClickListener( v -> {

                isAllFieldsChecked = checkAllFields();

                if (isAllFieldsChecked) {

                    String firstName = ted_first_name.getText().toString().trim();
                    String lastName = ted_last_name.getText().toString().trim();
                    String phone = tedPhone.getText().toString().trim();
                    String idnumber = ted_id_number.getText().toString().trim();
                    String relationship = ted_relationship.getText().toString().trim();

                    PrefManager.saveNextOfKinDetails(1, firstName,lastName, Integer.parseInt(idnumber), phone,relationship,1, getContext());
                    PropertyUtils.setHasNextOfKin(true);
                    NextOfKin kin = new NextOfKin(1, firstName,lastName, Integer.parseInt(idnumber), phone,relationship,1);

                    DataSynchronizationService.postNextOfKin(kin,getContext());
//
//                    Snackbar snackbar = Snackbar.make(constraintLayout, "Next of Kin updated successfully", Snackbar.LENGTH_SHORT);
//                    snackbar.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
//                    snackbar.setBackgroundTint(ContextCompat.getColor(getContext(),R.color.inactive_bottom_nav));
//                    snackbar.show();
                    NavHostFragment.findNavController(this).popBackStack();
                }

        });
        // Inflate the layout for this fragment
        return view;
    }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // On selecting a spinner item
            String item = parent.getItemAtPosition(position).toString();

            // Showing selected spinner item

        }

        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }

    private boolean checkAllFields() {
        String required = "This field is required";
        if (ted_first_name.length() == 0) {
            ted_first_name.setError(required);
            return false;
        }
        if (ted_last_name.length() == 0) {
            ted_last_name.setError(required);
            return false;
        }

        if (ted_id_number.length() < 7 || ted_id_number.length() > 9 ){
            ted_id_number.setError("Input a valid ID number");
            return false;

        }
        if (tedPhone.length() == 0) {
            tedPhone.setError(required);
            return false;
        } else if (tedPhone.length() < 10) {
            tedPhone.setError("Ensure the number is correct");
            return false;
        }

        return  true;

    }

}