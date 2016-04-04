package com.rspn.cryptotool.decrypt;

import com.rspn.cryptotool.utils.CTUtils;

import java.util.Hashtable;

public class VigenereDecryption {
    public static char[] plainText;
    private static char[] alphabet = CTUtils.getAlphabet();
    private static Hashtable<Character, Integer> charToInt = new Hashtable<>();
    private static boolean initialized = false;

    static {
        if (!initialized) {
            for (int i = 0; i < alphabet.length / 2; i++) {
                charToInt.put(alphabet[i], i);
            }
            for (int i = 26; i < alphabet.length; i++) {
                charToInt.put(alphabet[i], i % 26);
            }
            initialized = true;
        }
    }

    public static String runDecryption(String cypherText, String keyword, boolean keepWhitespaces) {
        int length = cypherText.length();
        String fmtKeyword = CTUtils.formatKeyword(length, keyword);
        plainText = new char[length];
        if (!CTUtils.retainCase) {
            cypherText = cypherText.toUpperCase();
            fmtKeyword = fmtKeyword.toUpperCase();
        }

        int keyPosition = 0;
        for (int i = 0; i < length; i++) {
            char currCypherChar = cypherText.charAt(i);//current character of the plaintext
            boolean upper = Character.isUpperCase(currCypherChar);
            char currKeywordChar = fmtKeyword.charAt(keyPosition);//current character in the keyword that now matches the length of the plaintext
            if (charToInt.containsKey(currCypherChar)) {//if it's alphabetic character
                int cypher = (charToInt.get(currCypherChar));
                int key = (charToInt.get(currKeywordChar));
                int plain = (((cypher - key) % 26) + 26) % 26;//to get a positive value of java modulus
                char decryptedChar = alphabet[plain];
                if (CTUtils.retainCase)//if we need to retain the case then convert it to the case plaintext's character
                    decryptedChar = (upper) ? Character.toUpperCase(decryptedChar) : Character.toLowerCase(decryptedChar);
                plainText[i] = decryptedChar;
                keyPosition++;
            } else if (currCypherChar == '\n') {
                plainText[i] = '\n';
            } else {//all other non alphacharacter will remain unchanged
                plainText[i] = currCypherChar;
            }
        }
        if (keepWhitespaces)
            return new String(plainText);
        else
            return new String(plainText).replaceAll(" ", "");


    }

}
