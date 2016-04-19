package com.rspn.cryptotool.passwordgenerator;

import com.rspn.cryptotool.utils.CTUtils;

public class PronounceablePasswordGenerator {
    private static final int NUMBER_OF_PASSWORDS = 10;
    private static final double SIGMA = 125729.0;
    private static final int CHARACTER_COUNT = 26;
    //    // Pick a random starting point.
//    pik = Math.random(); // random number [0,1]
//    ranno = pik * 125729.0;
//    sum = 0;
//    for (c1=0; c1 < 26; c1++) {
//        for (c2=0; c2 < 26; c2++) {
//            for (c3=0; c3 < 26; c3++) {
//                sum += _trigram[c1][c2][c3];
//                if (sum > ranno) {
//                    output += _alphabet.charAt(c1);
//                    output += _alphabet.charAt(c2);
//                    output += _alphabet.charAt(c3);
//                    c1 = 26; // Found start. Break all 3 loops.
//                    c2 = 26;
//                    c3 = 26;
//                } // if sum
//            } // for c3
//        } // for c2
//    } // for c1
//    // Now do a random walk.
//    nchar = 3;
//    while (nchar < pwl) {
//        c1 = _alphabet.indexOf(output.charAt(nchar-2));
//        c2 = _alphabet.indexOf(output.charAt(nchar-1));
//        sum = 0;
//        for (c3=0; c3 < 26; c3++)
//            sum += _trigram[c1][c2][c3];
//        if (sum == 0) {
//            //alert("sum was 0, outut="+output);
//            break;	// exit while loop
//        }
//        //pik = ran.nextDouble();
//        pik = Math.random();
//        ranno = pik * sum;
//        sum = 0;
//        for (c3=0; c3 < 26; c3++) {
//            sum += _trigram[c1][c2][c3];
//            if (sum > ranno) {
//                output += _alphabet.charAt(c3);
//                c3 = 26; // break for loop
//            } // if sum
//        } // for c3
//        nchar ++;
//    } // while nchar
//
//    return output;
//} // pronounceable


    public static String generatePasswords(int passwordLength) {
        double randomNumber = Math.random() * SIGMA;
        double sum = 0;
        char[] alphabet = CTUtils.getAlphabet();
        StringBuilder stringBuilder = new StringBuilder();
        for (int character1 = 0; character1 < CHARACTER_COUNT; character1++) {
            for (int character2 = 0; character2 < CHARACTER_COUNT; character2++) {
                for (int character3 = 0; character3 < CHARACTER_COUNT; character3++) {
                    sum += Characters.trigram[character1][character2][character3];
                    if (sum > randomNumber) {
                        stringBuilder.append(alphabet[character1]);
                        stringBuilder.append(alphabet[character2]);
                        stringBuilder.append(alphabet[character3]);
                        character3 = 26;
                        character1 = 26;
                        character2 = 26;
                    }
                }
            }
        }

        String currentPassword = stringBuilder.toString();
        int numberOfCharacter = 3;
        while (numberOfCharacter < passwordLength) {
            int character1 = CTUtils.charGetInt(currentPassword.charAt(numberOfCharacter - 2));
            int character2 = CTUtils.charGetInt(currentPassword.charAt(numberOfCharacter - 1));
            sum = 0;
            for (int character3 = 0; character3 < CHARACTER_COUNT; character3++) {
                sum += Characters.trigram[character1][character2][character3];
            }
            if (sum == 0) {
                break;
            }
            randomNumber = Math.random() * sum;
            sum = 0;
            for (int character3 = 0; character3< CHARACTER_COUNT; character3++) {
                sum += Characters.trigram[character1][character2][character3];
                if (sum > randomNumber) {
                    stringBuilder.append(alphabet[character3]);
                    character3 = 26; // break for loop
                }
            }
            numberOfCharacter++;
        }
        return stringBuilder.toString();
    }
}