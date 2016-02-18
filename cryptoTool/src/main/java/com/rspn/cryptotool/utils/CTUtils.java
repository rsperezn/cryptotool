package com.rspn.cryptotool.utils;

import java.util.Hashtable;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

public class CTUtils extends ActionBarActivity {
    //provides Utility methods and variables that will be use by other classes

    public static final String TAG = "CryptoTool";
    private static char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
            'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    public static Hashtable<Character, Integer> charToInt = new Hashtable<>();
    private static boolean initialized = false;
    private static double[] charProbs = {0.08167, 0.0492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094, 0.06966, 0.00153, 0.00772,
            0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758, 0.00978,
            0.02360, 0.00150, 0.01974, 0.00074};
    public static double EnglishIC = 0.065;

    public static boolean retainCase = false;
    public static boolean vibrate = false;
    public static double windowWidth = 0;
    public static double windowHeight = 0;

    public static char[] getAlphabet() {
        return alphabet;
    }

    public static char getChar(int i) {
        return alphabet[i];
    }

    public static void initialize() {
        if (!initialized) {
            //for upper case characters
            for (int i = 0; i < alphabet.length / 2; i++) {
                charToInt.put(alphabet[i], i);
            }
            //for lower case with the same upper case equivalent
            for (int i = 26; i < alphabet.length; i++) {
                charToInt.put(alphabet[i], i % 26);
            }
            initialized = true;
        }
    }

    public static String formatKeyword(int length, String keyword) {
        char[] result = new char[length];
        int keylen = keyword.length();
        for (int i = 0; i < result.length; i++) {
            result[i] = keyword.charAt(i % keylen);// 0 mod 0 will throw an error
        }
        return (new String(result));
    }

    public enum EType {//Encryption Types
        VIGENERE, CAESARS, NULL
    }

    public final static String ET = "EncryptedText";
    public final static String PT = "PlainText";
    public final static String DT = "DecryptedText";
    public final static String BET = "BrokenEncryptionText";

    public final static String EA = "EncryptActivity";
    public final static String DA = "DecryptActivity";
    public final static String BEA = "BreakEncryptionActivity";


    public static int charGetInt(char c) {
        return charToInt.get(c);
    }

    public static double charGetProb(char c) {
        return charProbs[charGetInt(c)];
    }

    public static double getProb(int i) {
        return charProbs[i];
    }

    public static String sanitize(String Text) {
        String result = Text.replaceAll("\\s+", "").toUpperCase().replaceAll("[^\\p{ASCII}]", "").replaceAll("\\d", "").replaceAll("[^a-zA-Z]", "");
        return result;
    }

    public static String bytesToHex(byte[] b) {
        char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuffer buf = new StringBuffer();
        for (int j = 0; j < b.length; j++) {
            buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
            buf.append(hexDigit[b[j] & 0x0f]);
        }
        return buf.toString();
    }

}
