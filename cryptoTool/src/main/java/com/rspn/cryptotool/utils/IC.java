package com.rspn.cryptotool.utils;

import java.util.Comparator;
import java.util.HashMap;


public class IC implements Comparable<IC>, Comparator<IC> {

    private int position;
    private float value;


    public IC() {
    }

    public IC(int inposition, float invalue) {
        position = inposition;
        value = invalue;
    }

    public float getVal() {
        return value;
    }

    public void setPos(int inpos) {
        position = inpos;
    }

    public void setVal(float inval) {
        value = inval;
    }

    public int getPos() {
        return position;
    }

    @Override
    public int compare(IC inIC1, IC inIC2) {
        double val1 = inIC1.getVal();
        double val2 = inIC2.getVal();
        double result = val1 - val2;
        if (result > 0) {
            return 1;
        } else if (result < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(IC another) {
        double compareQuantity = ((IC) another).getVal();
        double result = value - compareQuantity;
        if (result > 0) {
            return 1;
        } else if (result < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    //Static
    public static HashMap<String, Object> calculte(String text) {
        int[] charCount = new int[26];
        HashMap<String, Object> result = new HashMap<>();

        //count
        for (int i = 0; i < text.length(); i++) {
            charCount[CTUtils.charGetInt(text.charAt(i))] += 1;
        }
        result.put("charCount", charCount);

        ///sum
        int sum = 0;
        for (int i = 0; i < charCount.length; i++) {
            sum += charCount[i] * (charCount[i] - 1);
        }
        //IC
        float IC = (float) sum / (text.length() * (text.length() - 1));
        result.put("IC", IC);

        return result;
    }

}
