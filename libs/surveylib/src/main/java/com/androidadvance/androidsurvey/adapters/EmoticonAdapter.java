package com.androidadvance.androidsurvey.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidadvance.androidsurvey.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageButton;
import pl.droidsonroids.gif.GifImageView;

public class EmoticonAdapter extends BaseAdapter {

    private Context context;
    private TextView emotionView;
    private List<String> list;
    private int selectedIndex = -1;
    public EmoticonAdapter(Context context, TextView emotionView){
        this.context = context;
        list = new ArrayList<>();
        this.emotionView = emotionView;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GifImageView emojiView;

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.emoticon_layout, parent, false);
        }

        emojiView = convertView.findViewById(R.id.emoji);

        try {
            GifDrawable gifFromAssets = new GifDrawable( context.getAssets(), getItem(position).split(",")[1].trim());

            emojiView.setImageDrawable(gifFromAssets);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(selectedIndex == position){
            convertView.setBackground(context.getResources().getDrawable(R.drawable.circle_shape));
            emotionView.setText(getItem(position).split(",")[0].trim());
        }else{
            convertView.setBackgroundResource(0);
        }


        return convertView;
    }

    public void setList(List<String> list) {
        this.list = list;

        notifyDataSetChanged();
    }
}
