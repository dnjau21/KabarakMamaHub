package com.clinkod.kabarak.ui.settings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.clinkod.kabarak.R;
import com.clinkod.kabarak.models.SkinSetting;

import java.util.List;

/**
 * Created by Shadrack Mwai on 4/13/21. Clinkod Ltd,  shadmwai@gmail.com
 */
public class SkinAdapter extends BaseAdapter {
    private static final String TAG = SkinAdapter.class.getSimpleName();

    private Context context;
    private List<SkinSetting.SkinColor> colors;
    private int selectedColor;

    public SkinAdapter(Context context) {
        this.context = context;
        colors = SkinSetting.getColors();
        selectedColor = SkinSetting.getSkinColor();
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
    }

    @Override
    public int getCount() {
        return this.colors.size();
    }

    @Override
    public SkinSetting.SkinColor getItem(int i) {
        return this.colors.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        TextView textView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.skin_color_item_layout, null, false);
        }

        SkinSetting.SkinColor color = getItem(i);

        textView = view.findViewById(R.id.color);

        textView.setBackgroundColor(Color.parseColor(color.getDisplayColor()));
        if(color.getValue() == selectedColor){
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.circle_selected);

            drawable.setColorFilter(Color.parseColor(color.getDisplayColor()), PorterDuff.Mode.DST_ATOP);
            textView.setBackground(drawable);

        }else{
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.circle);
            drawable.setColorFilter(Color.parseColor(color.getDisplayColor()), PorterDuff.Mode.DST_ATOP);
            textView.setBackground(drawable);
        }

        return view;
    }

}
