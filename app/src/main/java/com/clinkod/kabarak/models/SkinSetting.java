package com.clinkod.kabarak.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shadrack Mwai on 4/13/21. Clinkod Ltd,  shadmwai@gmail.com
 */
public class SkinSetting {
    private static final String SKIN_COLOR = "skin_color";
    public static final String SKIN_COLOR_UPDATED = "skin_color_updated";

    public static final int WHITE = 0x00;
    public static final int WHITE_YELLOW = 0x01;
    public static final int YELLOW = 0x02;
    public static final int BROWN_ONE = 0x03;
    public static final int BROWN = 0x04;
    public static final int BLACK = 0x05;


    public static void setSkinColor(int skinColor) {
        PropertyUtils.putProperty(SKIN_COLOR, skinColor);
        PropertyUtils.putProperty(SKIN_COLOR_UPDATED, true);
    }

    public static int getSkinColor() {
        return PropertyUtils.getIntValue(SKIN_COLOR, WHITE);
    }

    public static List<SkinColor> getColors() {
        List<SkinColor> colors = new ArrayList<>();

        colors.add(new SkinColor(WHITE, "#ffffffff"));
        colors.add(new SkinColor(WHITE_YELLOW, "#ffffe8c8"));
        colors.add(new SkinColor(YELLOW, "#fff4d795"));
        colors.add(new SkinColor(BROWN, "#ffc21a"));
        colors.add(new SkinColor(BROWN_ONE, "#ffc88e49"));
        colors.add(new SkinColor(BLACK, "#ff634523"));

        return colors;
    }

    public static class SkinColor {
        private int value;
        private String displayColor;

        public SkinColor(int value, String displayColor) {
            this.value = value;
            this.displayColor = displayColor;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getDisplayColor() {
            return displayColor;
        }

        public void setDisplayColor(String displayColor) {
            this.displayColor = displayColor;
        }
    }
}
