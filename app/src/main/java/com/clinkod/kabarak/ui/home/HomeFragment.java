package com.clinkod.kabarak.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentTransaction;
import com.androidadvance.androidsurvey.SurveyActivity;
import com.clinkod.kabarak.R;
import com.clinkod.kabarak.Registration;
import com.clinkod.kabarak.beans.ObjectBox;
import com.clinkod.kabarak.models.BioData;
import com.clinkod.kabarak.models.BpAndHeartRate;
import com.clinkod.kabarak.models.BpAndHeartRate_;
import com.clinkod.kabarak.models.Mother;
import com.clinkod.kabarak.preference.PrefManager;
import com.clinkod.kabarak.ui.MainActivityFragment;
import com.clinkod.kabarak.ui.bioinfo.BioInfoFragment;
import com.clinkod.kabarak.ui.onboarding.PersonalInfoFragment;
import com.clinkod.kabarak.utils.Utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import io.objectbox.Box;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class HomeFragment extends MainActivityFragment {

    private BpAndHeartRate hourlyDataQuery, dateDataQuery;
    private Box<BpAndHeartRate> hourlyDataBox;
    private TextView systolic, diastolic, heartRate, greeting, username, profilePromt, bpTimeTaken, hrTimeTaken;
    BioData bioData;
    Mother mother;
    ImageView profileIcon;

    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final String PERSONAL = PersonalInfoFragment.class.getSimpleName();

    private CardView cardView;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);


        systolic = root.findViewById(R.id.txtSysValue);
        diastolic = root.findViewById(R.id.txtDiaValue);
        heartRate = root.findViewById(R.id.txtHrValue);
        greeting = root.findViewById(R.id.greeting);
        username = root.findViewById(R.id.username);
        bpTimeTaken = root.findViewById(R.id.txtBpTime);
        hrTimeTaken = root.findViewById(R.id.txtHrTime);
        profilePromt = root.findViewById(R.id.profilePrompt);
        profileIcon = root.findViewById(R.id.icon_profile);
        setGreetings();
        checkUserProfile();
        loadDataQueries();
        goToUserProfile();

        profilePromt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Registration.class);
                startActivity(intent);
            }
        });


//        TODO - If no value present hide the UI elements


        return root;
    }

    private void goToUserProfile() {

        profileIcon.setOnClickListener(new View.OnClickListener() {
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void loadDataQueries() {
        hourlyDataBox = ObjectBox.get().boxFor(BpAndHeartRate.class);
        hourlyDataQuery = hourlyDataBox.query().orderDesc(BpAndHeartRate_._id).build().findFirst();


        if (hourlyDataQuery != null) {
            int systolicBp = hourlyDataQuery.getSystolicBp();
            int diastolicBp = hourlyDataQuery.getDiastolicBp();
            int latestHr = hourlyDataQuery.getHeartRate();
            Date dataTime = hourlyDataQuery.getTimeTaken();
            Date currentTime = Calendar.getInstance().getTime();

            DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
            DateFormat hourFormat = new SimpleDateFormat("HH:MM aaa");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-YYYY");


             String entryDate = dateFormat.format(dataTime);
             String today = dateFormat.format(currentTime);


            int dateResult = dataTime.compareTo(currentTime);

            if (entryDate.equals(today)) {
                String todayTime = hourFormat.format(dataTime);
                bpTimeTaken.setText("Today: " + todayTime);
                hrTimeTaken.setText(todayTime);

            } else {
                DateFormat pastDateFormat = new SimpleDateFormat("dd-MM-YYYY");
                String pastTime = pastDateFormat.format(dataTime);
                bpTimeTaken.setText(pastTime);
                hrTimeTaken.setText(pastTime);

            }


            systolic.setText(String.valueOf(systolicBp));
            diastolic.setText(String.valueOf(diastolicBp));
            heartRate.setText(String.valueOf(latestHr));


        }
    }

    private void checkUserProfile() {
        try {
             String name = PrefManager.getName(getActivity());

            if (name != null) {
                profilePromt.setVisibility(View.GONE);
                username.setText(name);
            } else {
                profilePromt.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {

        }
    }

    private void setGreetings() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting.setText("Good Morning");

        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting.setText("Good Afternoon");

        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            greeting.setText("Good Evening");

        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            greeting.setText("Good Night");
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void activityMoodSurvey() {
        String onboardingQuestions = Utils.loadSurveyJson(getContext(), "activity_mood.json");

        Intent i_survey = new Intent(getContext(), SurveyActivity.class);
        i_survey.putExtra("json_survey", onboardingQuestions);
        startActivityForResult(i_survey, 1223);
    }
}