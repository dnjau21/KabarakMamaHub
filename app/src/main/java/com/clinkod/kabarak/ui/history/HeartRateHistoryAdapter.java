package com.clinkod.kabarak.ui.history;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.clinkod.kabarak.R;
import com.clinkod.kabarak.models.BpAndHeartRate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class HeartRateHistoryAdapter extends BaseAdapter {
    private static final String TAG = HeartRateHistoryAdapter.class.getSimpleName();

    private Activity activity;
    //private Map<Date, String> list;
    private Date[] keys;

    private Map<Date, BpAndHeartRate> list;

    public HeartRateHistoryAdapter(Activity activity) {
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
            notifyDataSetChanged();
        }
    }

    public void clearList() {

        // Log.i(TAG, "History List cleared");
        list.clear();
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
            convertView = activity.getLayoutInflater().inflate(R.layout.heart_rate_history_item_layout, parent, false);

        }

        viewHolder.date = (TextView) convertView.findViewById(R.id.date);
        viewHolder.lblHr = (TextView) convertView.findViewById(R.id.lblHr);
        viewHolder.hr= (TextView) convertView.findViewById(R.id.hr);
        viewHolder.status = (TextView) convertView.findViewById(R.id.hrstatus);

        BpAndHeartRate item = getItem(position);
        int hr = item.getHeartRate();


        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        viewHolder.date.setText(dateFormat.format(keys[position]));
        viewHolder.hr.setText(String.valueOf(hr));
        viewHolder.status.setText("Normal");
        viewHolder.status.setBackground(ContextCompat.getDrawable(activity.getApplicationContext(),R.drawable.vitals_status_normal_indicator));

        return convertView;
    }


    class ViewHolder {
        TextView date, lblHr, hr, status;

    }
}
