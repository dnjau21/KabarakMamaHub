package com.clinkod.kabarak.models.enums;

import java.util.HashMap;
import java.util.Map;

public enum Medication {
    OneDaily(0),
    TwoDaily(1),
    MoreThanTwoDaily(2),
    OnePerWeek(3),
    TwoPerWeek(4),
    MoreThanTwoPerWeek(5),
    Other(6),
    None(7);

    private static final Map<Integer, Medication> lookup = new HashMap<Integer, Medication>();

    static {
        for(Medication medication : Medication.values()){
            lookup.put(medication.getValue(), medication);
        }
    }

    private int value;

    Medication(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Medication get(int value){
        return lookup.get(value);
    }
}
