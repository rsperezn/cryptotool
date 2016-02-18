package com.rspn.cryptotool.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

public class NGram {
    Hashtable<String, Double> ngrams = new Hashtable<String, Double>();
    int len = 0;//ngram length
    double sum = 0;//total number of ngrams in the training sample
    double floor;

    public NGram(String fileName, int len) {
        this.len = len;//the length on the ngram that the file has
        try {
            InputStream stream = this.getClass().getResourceAsStream("/res/raw/" + fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line = br.readLine();
            //build the hash line by line
            while (line != null) {
                String key = line.split(" ")[0];
                double value = (double) Integer.parseInt(line.split(" ")[1]);
                ngrams.put(key, value);
                sum += value;
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            Log.i(CTUtils.TAG, e.toString());
        }

        Log.i(CTUtils.TAG, "Sum " + String.valueOf(sum));

        //calculate probabilities
        for (String key : ngrams.keySet()) {
            double value = Math.log10((double) ngrams.get(key) / sum);
            ngrams.put(key, value);

        }
        floor = Math.log10(0.01 / len);
    }

    public NGram() {
        try {
            InputStream stream = this.getClass().getResourceAsStream("/res/raw/quadgram_probabilities.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line = br.readLine();
            //fillthe hash line by line
            while (line != null) {
                String key = line.split(" ")[0];
                double value = Double.parseDouble(line.split(" ")[1]);
                ngrams.put(key, value);
                line = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            Log.i(CTUtils.TAG, e.toString());
        }
    }


    public double score(String text, int len) {
        double score = 0;
        for (int i = 0; i < text.length() - len + 1; i++) {
            String sub = text.substring(i, i + len);
            if (ngrams.containsKey(sub)) {
                score += ngrams.get(sub);
            } else {
                score += floor;
            }
        }
        return score;
    }

}
