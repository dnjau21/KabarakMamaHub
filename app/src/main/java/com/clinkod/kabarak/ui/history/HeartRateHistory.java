package com.clinkod.kabarak.ui.history;

import android.os.Bundle;
import android.widget.ListView;

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

public class HeartRateHistory extends Fragment {
    private ListView historyList;
    private HeartRateHistoryAdapter heartRateHistoryAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_heart_rate_history, container, false);
        historyList = view.findViewById(R.id.hrHistory);

        heartRateHistoryAdapter = new HeartRateHistoryAdapter(getActivity());
        historyList.setAdapter(heartRateHistoryAdapter);

        Box<BpAndHeartRate> dataSet = ObjectBox.get().boxFor(BpAndHeartRate.class);
        List<BpAndHeartRate> dataSetList = dataSet.getAll();

        return view;

    }

    public void onDateChanged(Date startDate, Date endDate) {
        new Thread(()->{
           heartRateQueryByDate(startDate, endDate);
        }).start();
        // Log.i(TAG, "StartDate " + startDate + "EndDate" + endDate);
    }

    private void updateHistoryList() {
        new Thread(() -> {
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

                heartRateQueryByDate(startDate, endDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void heartRateQueryByDate(Date startOfDay, Date endOfDay) {
        Box<BpAndHeartRate> hourlyDataBox = ObjectBox.get().boxFor(BpAndHeartRate.class);
        QueryBuilder<BpAndHeartRate> bpByDate = hourlyDataBox.query();
        // Log.i(TAG, "object box is initialized!");
        bpByDate.between(BpAndHeartRate_.timeTaken, startOfDay, endOfDay);
        List<BpAndHeartRate> byDateResults = bpByDate.build().find();

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                try {
                    heartRateHistoryAdapter.clearList();

                    // Log.i(TAG, "UI Thread is reached");
                    for (final BpAndHeartRate hourlyData : byDateResults) {

                       heartRateHistoryAdapter.addItem(hourlyData.getTimeTaken(),
                                hourlyData);
                    }
                } catch (Exception e) {
                    // Log.e("MY APP", "Exception", e);
                }

            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateHistoryList();
    }
}