package com.rspn.cryptotool.passwordgenerator;

import com.rspn.cryptotool.passwordgenerator.Characters.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PasswordGenerator {

    public static String generatePassword(int passwordLength, boolean excludeSimilarLookingCharacters, Characters.Types... requiredTypes) throws Exception {


        boolean requiresAtLeastOneEachType = requiredTypes.length == Types.values().length;
        if (!requiresAtLeastOneEachType) {
            String strongPassword = "";
            for (int i = 0; i < passwordLength; i++) {
                char randomCharacter = CharacterGenerator.getRandomCharacter(requiredTypes);
                if (excludeSimilarLookingCharacters && CharacterGenerator.isSimilarLookingCharacter(randomCharacter)) {
                    randomCharacter = CharacterGenerator.getNonSimilarLookingCharacter();
                }
                strongPassword += randomCharacter;
            }
            return strongPassword;
        } else {
            char[] strongPasswordArr = createPasswordWithRequiredTypes(passwordLength, excludeSimilarLookingCharacters);

            // fill the rest of the array with random Characters
            for (int i = 0; i < passwordLength; i++) {
                if (strongPasswordArr[i] == '\0') {
                    strongPasswordArr[i] = CharacterGenerator.getRandomCharacter();
                }
            }
            return new String(strongPasswordArr);
        }
    }

    private static char[] createPasswordWithRequiredTypes(int passwordLength, boolean excludeSimilarLookingCharacters) throws Exception {
        Random random = new Random();
        List<Types> requiredTypes;
        char[] strongPasswordArr = new char[passwordLength];
        requiredTypes = Arrays.asList(Characters.Types.values());

        int characterTypesLength = Characters.getTypesLength();
        List<Integer> availablePositions = createAvailablePositions(passwordLength);
        // get a random character of each type and put it at a random position
        for (int i = 0; i < characterTypesLength; i++) {
            int positionForRandomCharacter = availablePositions.get(random.nextInt(availablePositions.size()));
            Types requiredType = requiredTypes.get(i);
            char randomCharacter = CharacterGenerator.getRandomCharacter(requiredType);
            if (CharacterGenerator.isSimilarLookingCharacter(randomCharacter) && excludeSimilarLookingCharacters) {
                randomCharacter = CharacterGenerator.getNonSimilarLookingCharacter(requiredType);
            }

            strongPasswordArr[positionForRandomCharacter] = randomCharacter;
            availablePositions.remove((Integer) positionForRandomCharacter);
        }
        return strongPasswordArr;
    }

    private static List<Integer> createAvailablePositions(int passwordLength) {
        List<Integer> availablePositions = new ArrayList<>();
        for (int i = 0; i < passwordLength; i++) {
            availablePositions.add(i);
        }
        return availablePositions;
    }

}
