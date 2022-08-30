package com.clinkod.kabarak.ui.history;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.androidadvance.androidsurvey.SurveyActivity;
import com.clinkod.kabarak.R;
import com.clinkod.kabarak.models.BpAndHeartRate;
import com.clinkod.kabarak.utils.Utils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class BloodPressureHistoryListAdapter extends BaseAdapter {

    private static final String TAG = BloodPressureHistoryListAdapter.class.getSimpleName();
    private static final int ACTIVITY_MOOD_REQUEST = 1340;
    private Activity activity;
    //private Map<Date, String> list;
    private Date[] keys;
    private Context context;

    public BloodPressureHistoryListAdapter(Context context) {
        this.context = context;
    }

    private Map<Date, BpAndHeartRate> list;


    public BloodPressureHistoryListAdapter(Activity activity) {
        this.activity = activity;
        this.list = new TreeMap<>(new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return -o1.compareTo(o2);
            }
        });
    }

    public void addItem(Date date, BpAndHeartRate value) {
        if (!list.containsKey(date)) {
            list.put(date, value);
            keys = list.keySet().toArray(new Date[list.size()]);
        }
    }


    public void clearList() {
        list.clear();
        notifyDataSetChanged();

    }
    public void addData(Map<Date, BpAndHeartRate> data) {
        list.clear();
        list.putAll(data);
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public BpAndHeartRate getItem(int position) {
        return list.get(keys[position]);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.blood_pressurre_history_item_layout, parent, false);
        }

        viewHolder.date = (TextView) convertView.findViewById(R.id.date);
        viewHolder.lblSys = (TextView) convertView.findViewById(R.id.lblSys);
        viewHolder.sys = (TextView) convertView.findViewById(R.id.sys);
        viewHolder.lblDia = (TextView) convertView.findViewById(R.id.lblDia);
        viewHolder.dia = (TextView) convertView.findViewById(R.id.dia);
        //viewHolder.status = (TextView) convertView.findViewById(R.id.status);
        viewHolder.captureActivity = (Button) convertView.findViewById(R.id.captureActivity);


        BpAndHeartRate item = getItem(position);
        int systolic = item.getSystolicBp();
        int diastolic = item.getDiastolicBp();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        viewHolder.date.setText(dateFormat.format(keys[position]));
        viewHolder.sys.setText(String.valueOf(systolic));
        viewHolder.dia.setText(String.valueOf(diastolic));
        View finalConvertView = convertView;
        viewHolder.captureActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String onboardingQuestions = Utils.loadSurveyJson(activity.getApplicationContext(), "activity_mood.json");
                Intent i_survey = new Intent(activity.getApplicationContext(), SurveyActivity.class);
                i_survey.putExtra("json_survey", onboardingQuestions);
                i_survey.putExtra("mother_id",item.getMother_id());
                i_survey.putExtra("mother_random_id",item.getRandom_id());
                activity.startActivityForResult(i_survey,ACTIVITY_MOOD_REQUEST);
            }
        });

//        if ((systolic >= 90 && systolic <= 120) || (diastolic > 60 && diastolic <= 80)) {
//            viewHolder.status.setText("Normal");
//            viewHolder.status.setTextColor(Color.WHITE);
//            viewHolder.status.setBackground(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.vitals_status_normal_indicator));
//        } else if ((systolic > 120 && systolic < 130) || (diastolic >= 60 && diastolic <= 80)) {
//            viewHolder.status.setText("Elevated");
//            viewHolder.status.setBackground(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.vitals_status_elevated_indicator));
//        }
//        else if ((systolic > 130 && systolic <= 140) || (diastolic >= 60 && diastolic <= 80)) {
//            viewHolder.status.setText("High");
//            viewHolder.status.setBackground(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.vitals_status_high_indicator));
//        } else if (systolic < 90 && diastolic < 60) {
//            viewHolder.status.setText("Low");
//            viewHolder.status.setTextColor(Color.WHITE);
//            viewHolder.status.setBackground(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.vitals_status_extreme_indicator));
//        }else if (systolic > 180 && diastolic > 120) {
//            viewHolder.status.setText("Hypertensive");
//            viewHolder.status.setTextColor(Color.WHITE);
//            viewHolder.status.setBackground(ContextCompat.getDrawable(activity.getApplicationContext(), R.drawable.vitals_status_hypertensive_indicator));
//        }
        return convertView;
    }


    class ViewHolder {
        TextView date, sys, dia, lblSys, lblDia, status;
        Button captureActivity;

    }


}
