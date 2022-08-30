package com.clinkod.kabarak.ui.history;

import android.content.Context;

import androidx.fragment.app.*;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    Context context;
    HistoryFragment historyFragment;
    List<Fragment> fragmentList;
    private final List<Fragment> fragmentArrayList = new ArrayList<>();
    private final List<String> fragmentListTitle = new ArrayList<>();
    private static final int NUM_PAGES = 3;
    public final String[] fragmentNames = new String[]{
            "Blood Pressure",
            "Heart rate",
            "Weight"
    };




    public ViewPagerAdapter(FragmentActivity fm) {
        super(fm);
    }

    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: {
                return new BloodPressureHistory();
            }

            case 1: {
                return new HeartRateHistory();
            }

            case 2: {
                return new WeightHistory();
            }
            default: {
                return new BloodPressureHistory();
            }
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}

