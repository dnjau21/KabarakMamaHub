package com.clinkod.kabarak.models.enums;

import java.util.HashMap;
import java.util.Map;

public enum Mood {
    Happy(0),
    Stressed(1),
    Anxious(2),
    Sad(3),
    Angry(4),
    Tense(5),
    Depressed(6),
    Neutral(7);

    private static final Map<Integer, Mood> lookup = new HashMap<Integer, Mood>();

    static {
        for(Mood frequency : Mood.values()){
            lookup.put(frequency.getValue(), frequency);
        }
    }


    private int value;
    Mood(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Mood get(int value){
        return lookup.get(value);
    }
}
