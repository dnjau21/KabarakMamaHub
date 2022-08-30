package com.clinkod.kabarak.models.enums;

import java.util.HashMap;
import java.util.Map;

public enum SleepPattern {
    MoreThanEightHours(0),
    SixHours(1),
    FourHours(2),
    LessThanFourHours(4);

    private static final Map<Integer, SleepPattern> lookup = new HashMap<Integer, SleepPattern>();

    static {
        for(SleepPattern sleepPattern : SleepPattern.values()){
            lookup.put(sleepPattern.getValue(), sleepPattern);
        }
    }

    private int value;
    SleepPattern(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static SleepPattern get(int value){
        return lookup.get(value);
    }
}
