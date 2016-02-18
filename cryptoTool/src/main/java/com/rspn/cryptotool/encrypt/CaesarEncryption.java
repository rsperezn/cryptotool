package com.rspn.cryptotool.encrypt;

import java.util.Hashtable;

import com.rspn.cryptotool.utils.CTUtils;

public class CaesarEncryption {
    private static char[] alphabet = CTUtils.getAlphabet();
    private static Hashtable<Character, Integer> charToint = new Hashtable<>();
    private static boolean initialized = false;
    private static char[] encryptedText;
    private static boolean retainCase;

    public static void initComponents() {
        if (!initialized) {
            //upper case letter
            for (int i = 0; i < alphabet.length; i++) {
                charToint.put(alphabet[i], i);
            }
            initialized = true;
        }
    }

    public static String runEncryption(String plainText, int delta, boolean whitespaces) {
        encryptedText = new char[plainText.length()];
        retainCase = CTUtils.retainCase;
        if (!retainCase) {
            plainText = plainText.toUpperCase();
        }

        for (int i = 0; i < plainText.length(); i++) {
            if (charToint.containsKey(plainText.charAt(i))) {
                boolean upper = Character.isUpperCase(plainText.charAt(i));
                int pos = (charToint.get(plainText.charAt(i)) + delta) % 52;
                //make sure that it doesn't change
                if (upper && pos > 25)
                    pos = pos - 26;
                else if (!upper && pos < 26)
                    pos = pos + 26;
                encryptedText[i] = alphabet[pos];
            } else if (plainText.charAt(i) == '\n') {
                encryptedText[i] = '\n';
            } else {
                encryptedText[i] = plainText.charAt(i);
            }
        }
        if (whitespaces)
            return new String(encryptedText);
        else
            return new String(encryptedText).replaceAll(" ", "");
    }


}
