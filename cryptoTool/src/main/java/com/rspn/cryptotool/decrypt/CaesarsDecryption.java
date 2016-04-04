package com.rspn.cryptotool.decrypt;

import java.util.Hashtable;

import com.rspn.cryptotool.utils.CTUtils;

public class CaesarsDecryption {
    private static char[] alphabet = CTUtils.getAlphabet();
    private static Hashtable<Character, Integer> charToint = new Hashtable<>();
    private static boolean initialized = false;

    static {
        if (!initialized) {
            for (int i = 0; i < alphabet.length; i++) {
                charToint.put(alphabet[i], i);
            }
            initialized = true;
        }
    }

    public static String runDecryption(String encryptedText, int delta, boolean whitespaces) {
        char[] decryptedText = new char[encryptedText.length()];

        if (!CTUtils.retainCase) {
            encryptedText = encryptedText.toUpperCase();
        }

        for (int i = 0; i < encryptedText.length(); i++) {
            boolean upper = Character.isUpperCase(encryptedText.charAt(i));
            if (charToint.containsKey(encryptedText.charAt(i))) {
                int pos = (((charToint.get(encryptedText.charAt(i)) - delta) % 52) + 52) % 52;
                //make sure that it doesn't change
                if (upper && pos > 25)
                    pos = pos - 26;
                else if (!upper && pos < 26)
                    pos = pos + 26;

                decryptedText[i] = alphabet[pos];
            } else if (encryptedText.charAt(i) == '\n') {
                decryptedText[i] = '\n';
            } else {
                decryptedText[i] = encryptedText.charAt(i);
            }
        }
        if (whitespaces)
            return new String(decryptedText);
        else
            return new String(decryptedText).replaceAll(" ", "");
    }

}
