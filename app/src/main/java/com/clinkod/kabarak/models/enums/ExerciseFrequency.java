package com.clinkod.kabarak.models.enums;

import java.util.HashMap;
import java.util.Map;

public enum ExerciseFrequency {
    NotAtAll(0),
    OnceAWeek(1),
    TwiceAWeek(2),
    ThriceAWeek(3),
    FourTimesAWeek(4),
    FiveTimesAWeek(5),
    SixTimesAWeek(6),
    Everyday(7);

    private static final Map<Integer, ExerciseFrequency> lookup = new HashMap<Integer, ExerciseFrequency>();

    static {
        for(ExerciseFrequency frequency : ExerciseFrequency.values()){
            lookup.put(frequency.getValue(), frequency);
        }
    }

    private int value;
    ExerciseFrequency(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }

    public static ExerciseFrequency get(int value){
        return lookup.get(value);
    }
}
