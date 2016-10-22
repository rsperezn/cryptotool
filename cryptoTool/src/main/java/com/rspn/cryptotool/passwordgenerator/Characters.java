package com.rspn.cryptotool.passwordgenerator;

import java.util.Random;

public class Characters {
    public static Types getRandomType() {
        Random random = new Random();
        int randomNum = random.nextInt(getTypesLength());
        if (randomNum == 0)
            return Types.UPPER_CASE;
        else if (randomNum == 1)
            return Types.LOWER_CASE;
        else if (randomNum == 2)
            return Types.DIGITS;
        else if (randomNum == 3)
            return Types.AMBIGUOUS_SYMBOLS;
        else
            return Types.SYMBOLS;
    }

    public static int getTypesLength() {
        return Types.values().length;
    }

    public static int getTypesLengthExcludingAmbiguousSymbols() {
        return getTypesLength() - 1;
    }

    public enum Types {UPPER_CASE, LOWER_CASE, DIGITS, SYMBOLS, AMBIGUOUS_SYMBOLS}
}
