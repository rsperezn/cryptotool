package com.rspn.cryptotool.passwordgenerator;

import com.rspn.cryptotool.passwordgenerator.Characters.Types;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {

    public static List<String> generatePassword(int passwordLength, int numberOfPasswords,
                                                boolean excludeSimilarLookingCharacters,
                                                Characters.Types... requiredTypes) throws Exception {

        List<String> strongPasswords = new ArrayList<>();

        for (int j = 0; j < numberOfPasswords; j++) {
            char[] strongPasswordArr = createPasswordWithRequiredTypes(passwordLength, requiredTypes, excludeSimilarLookingCharacters);
            fillBlankCharacters(passwordLength, excludeSimilarLookingCharacters, strongPasswordArr, requiredTypes);
            strongPasswords.add(new String(strongPasswordArr));
        }
        return strongPasswords;
    }

    private static char[] createPasswordWithRequiredTypes(int passwordLength,
                                                          Types[] requiredTypes, boolean excludeSimilarLookingCharacters) throws Exception {
        Random random = new Random();
        char[] strongPasswordArr = new char[passwordLength];

        List<Integer> availablePositions = createAvailablePositions(passwordLength);
        // get a random character of each type and put it at a random position
        for (Types requiredType : requiredTypes) {
            int positionForRandomCharacter = availablePositions.get(random.nextInt(availablePositions.size()));
            char randomCharacter = CharacterGenerator.getRandomCharacter(requiredType);
            if (mustExcludeSimilarLookingCharacter(excludeSimilarLookingCharacters, randomCharacter)) {
                randomCharacter = CharacterGenerator.getNonSimilarLookingCharacter(requiredType);
            }

            strongPasswordArr[positionForRandomCharacter] = randomCharacter;
            availablePositions.remove((Integer) positionForRandomCharacter);
        }
        return strongPasswordArr;
    }

    private static boolean mustExcludeSimilarLookingCharacter(boolean excludeSimilarLookingCharacters, char randomCharacter) {
        return CharacterGenerator.isSimilarLookingCharacter(randomCharacter) && excludeSimilarLookingCharacters;
    }

    private static List<Integer> createAvailablePositions(int passwordLength) {
        List<Integer> availablePositions = new ArrayList<>();
        for (int i = 0; i < passwordLength; i++) {
            availablePositions.add(i);
        }
        return availablePositions;
    }

    private static void fillBlankCharacters(int passwordLength, boolean excludeSimilarLookingCharacters, char[] strongPasswordArr, Types[] requiredTypes) throws Exception {
        for (int i = 0; i < passwordLength; i++) {
            if (strongPasswordArr[i] == '\0') {
                strongPasswordArr[i] = excludeSimilarLookingCharacters ? CharacterGenerator.getNonSimilarLookingCharacter(requiredTypes)
                        : CharacterGenerator.getRandomCharacter(requiredTypes);
            }
        }
    }

}
