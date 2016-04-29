package com.rspn.cryptotool.passwordgenerator;

import com.rspn.cryptotool.utils.CTUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PronounceablePasswordGenerator {
    private static final int consonantsCount = 21;
    private static final int vowelsCount = 5;
    private static Random random = new Random();

    public static List<String> generateCVVPassword(int passwordLength, int numberOfPasswords) {
        List<String> passwords = new ArrayList<>();
        StringBuilder stringBuilder;
        for (int i = 0; i < numberOfPasswords; i++) {
            stringBuilder = new StringBuilder();
            generateConstantVowelVowelSequence(passwordLength, stringBuilder);
            int extraCharacters = passwordLength % 3;
            if (extraCharacters > 0) {
                passwords.add(trimExtraCharacters(passwordLength, stringBuilder, extraCharacters));
            } else {
                passwords.add(stringBuilder.toString());
            }
        }

        return passwords;
    }

    public static List<String> generateCVPassword(int passwordLength, int numberOfPasswords) {
        List<String> passwords = new ArrayList<>();
        StringBuilder stringBuilder;
        for (int i = 0; i < numberOfPasswords; i++) {
            stringBuilder = new StringBuilder();
            generateConstantVowelSequence(passwordLength, stringBuilder);
            int extraCharacters = passwordLength % 2;
            if (extraCharacters > 0) {
                passwords.add(trimExtraCharacters(passwordLength, stringBuilder, extraCharacters));
            } else {
                passwords.add(stringBuilder.toString());
            }
        }

        return passwords;
    }

    private static void generateConstantVowelSequence(int passwordLength, StringBuilder stringBuilder) {
        int charactersInSequence = 2;
        for (int j = 0; j < passwordLength / charactersInSequence; j++) {
            stringBuilder.append(getRandomConsonant());
            stringBuilder.append(getRandomVowel());
        }
    }

    private static void generateConstantVowelVowelSequence(int passwordLength, StringBuilder stringBuilder) {
        int charactersInSequence = 3;
        for (int j = 0; j < passwordLength / charactersInSequence; j++) {
            stringBuilder.append(getRandomConsonant());
            stringBuilder.append(getRandomVowel());
            stringBuilder.append(getRandomVowel());
        }
    }

    private static String trimExtraCharacters(int passwordLength, StringBuilder stringBuilder, int extraCharacters) {
        return stringBuilder.toString().substring(0, passwordLength - extraCharacters);
    }

    private static char getRandomConsonant() {
        return CTUtils.getConsonants()[random.nextInt(consonantsCount)];
    }

    private static char getRandomVowel() {
        return CTUtils.getVowels()[random.nextInt(vowelsCount)];
    }
}