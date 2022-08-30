package com.clinkod.kabarak.ui.settings;

import android.content.Context;

import com.clinkod.kabarak.R;

public class PeriodicRadioButtonMapper {

    private Context context;

    public PeriodicRadioButtonMapper(Context context) {
        this.context = context;
    }

    public String mapToStrigFrom(int viewId) {
        String tag;

        switch (viewId) {
            case R.id.fivemin:
                tag = getStringResource(R.string.fivemin);
                break;
            case R.id.fifteenmin:
                tag = getStringResource(R.string.fifteenmin);
                break;
            case R.id.thirtymin:
                tag = getStringResource(R.string.thirtymin);
                break;
            case R.id.fourtyfivemin:
                tag = getStringResource(R.string.fourtyfivemin);
                break;
            case R.id.onehr:
                tag = getStringResource(R.string.onehr);
                break;
            case R.id.twohr:
                tag = getStringResource(R.string.twohr);
                break;
            case R.id.threehr:
                tag = getStringResource(R.string.threehr);
                break;
            case R.id.fourhr:
                tag = getStringResource(R.string.fourhr);
            case R.id.fivehr:
                tag = getStringResource(R.string.fivehr);
                break;
            case R.id.sixhr:
                tag = getStringResource(R.string.sixhr);
                break;
            default:
                tag = getStringResource(R.string.mapper_default_value);
                break;
        }

        return tag;
    }

    private String getStringResource(int stringId) {
        return context.getResources().getString(stringId);
    }
}
