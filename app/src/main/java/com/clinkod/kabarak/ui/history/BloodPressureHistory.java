package com.clinkod.kabarak.ui.history;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.clinkod.kabarak.R;
import com.clinkod.kabarak.beans.ObjectBox;
import com.clinkod.kabarak.models.BpAndHeartRate;
import com.clinkod.kabarak.models.BpAndHeartRate_;
import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class BloodPressureHistory extends Fragment {

    private ListView historyList;
    private BloodPressureHistoryListAdapter bloodPressureHistoryListAdapter;
    private Activity context;
    private static BloodPressureHistory instance;
    private Map<Date, BpAndHeartRate> list;
    public static BloodPressureHistory GetInstance()
    {
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        instance= this;
        View view = inflater.inflate(R.layout.fragment_blood_pressure_history, container, false);

        historyList = view.findViewById(R.id.history);
        Box<BpAndHeartRate> dataSet = ObjectBox.get().boxFor(BpAndHeartRate.class);
        List<BpAndHeartRate> dataSetList = dataSet.getAll();

        bloodPressureHistoryListAdapter = new BloodPressureHistoryListAdapter(getActivity());
        historyList.setAdapter(bloodPressureHistoryListAdapter);

        return view;

    }

    public void onDateChanged(Date startDate, Date endDate) {
           bloodPressureQueryByDate(startDate, endDate);
    }

    private void updateHistoryList() {

            try {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date startDate = calendar.getTime();

                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                Date endDate = calendar.getTime();

                bloodPressureQueryByDate(startDate, endDate);
            } catch (Exception e) {
                e.printStackTrace();
            }

    }


    public void bloodPressureQueryByDate(Date startOfDay, Date endOfDay) {
        Box<BpAndHeartRate> hourlyDataBox = ObjectBox.get().boxFor(BpAndHeartRate.class);
        QueryBuilder<BpAndHeartRate> bpByDate = hourlyDataBox.query();
        // Log.i(TAG, "object box is initialized!");

        bpByDate.between(BpAndHeartRate_.timeTaken, startOfDay, endOfDay);

        List<BpAndHeartRate> byDateResults = bpByDate.build().find();
        if(getActivity() != null ){
            getActivity().runOnUiThread(() -> {
                try {
                    bloodPressureHistoryListAdapter.clearList();
                    // Log.i(TAG, "UI Thread is reached");
                    for (final BpAndHeartRate hourlyData : byDateResults) {
                        bloodPressureHistoryListAdapter.addItem(hourlyData.getTimeTaken(),
                                hourlyData);
                    }
                    bloodPressureHistoryListAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    // Log.e("MY APP", "Exception", e);
                }

            });
        }


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {//Name of your activity
            this.context = (Activity) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        updateHistoryList();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // In fragment class callback

    }


}