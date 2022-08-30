package com.clinkod.kabarak.ui.history;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.clinkod.kabarak.R;
import com.clinkod.kabarak.models.PropertyUtils;
import com.clinkod.kabarak.services.DataSynchronizationService;
import com.clinkod.kabarak.ui.MainActivityFragment;
import com.clinkod.kabarak.utils.Utils;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class HistoryFragment extends MainActivityFragment implements DatePickerListener {

    private static final String TAG = HistoryFragment.class.getSimpleName();

    private HistoryViewModel historyViewModel;

    private ListView historyList;
    private TextView noDataView, manual;
    private BloodPressureHistoryListAdapter bloodPressureHistoryListAdapter;
    private HorizontalPicker picker;
    private  FloatingActionButton manualButton;
    private Activity activity;
    TabLayout tabLayout;
    ViewPager2 viewpager;
    private int[] imageList = {R.drawable.ic_blood_pressure, R.drawable.ic_heart_rate, R.drawable.ic_weight_scale};
    public final String[] fragmentNames = new String[]{"Blood Pressure", "Heart rate", "Temprature"};




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        historyList = v.findViewById(R.id.history);
        picker = v.findViewById(R.id.hPicker);

//        manual = v.findViewById(R.id.manual);
//        manualButton = v.findViewById(R.id.manual_add);

//        manualButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment measureFragment = new MeasureFragment();
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.nav_host_fragment, measureFragment);
//                ft.addToBackStack(null);
//                ft.commit();
//            }
//        });



        viewpager = v.findViewById(R.id.fPager);
        viewpager.setAdapter(new ViewPagerAdapter(getActivity()));

        tabLayout = v.findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewpager,(tab, position) -> tab.setText(fragmentNames[position])).attach();
//        setUpTabIcons();

        datePicker();
        try {
            if(Utils.isConnected()){
                if(!PropertyUtils.hasBeenSynch()){
                    DataSynchronizationService.getMothersBpReadings(getContext());
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return v;
    }

    private void datePicker() {
        picker.setListener(this)
                .setDays(365)
                .setOffset(10)
                .setMonthAndYearTextColor(getResources().getColor(R.color.primaryColor))
                .setDayOfWeekTextColor(getResources().getColor(R.color.primaryColor))
                .setDateSelectedTextColor(getResources().getColor(R.color.white))
                .setTodayDateBackgroundColor(getResources().getColor(R.color.green_600))
                .setTodayButtonTextColor(getResources().getColor(R.color.green_600))
                .setUnselectedDayTextColor(getResources().getColor(R.color.grey_300))
                .init();
        picker.setDate(new DateTime());
        picker.setBackgroundColor(0);
    }


    private void setUpTabIcons() {
        tabLayout.getTabAt(0).setIcon(imageList[0]);
        tabLayout.getTabAt(1).setIcon(imageList[1]);
        tabLayout.getTabAt(2).setIcon(imageList[2]);
    }


    public void onDateSelected(DateTime dateSelected) {
        Date dt = dateSelected.toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dt);
        Date selectedDateStart = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date selectedDateEnd = calendar.getTime();

        BloodPressureHistory frag = BloodPressureHistory.GetInstance();
        frag.onDateChanged(selectedDateStart, selectedDateEnd);;
//        BloodPressureHistory bloodPressureHistory = new BloodPressureHistory();
//        bloodPressureHistory.onDateChanged(selectedDateStart, selectedDateEnd, getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}