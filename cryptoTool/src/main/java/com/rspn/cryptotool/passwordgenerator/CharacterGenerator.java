package com.rspn.cryptotool.passwordgenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.rspn.cryptotool.passwordgenerator.Characters.Types;
import com.rspn.cryptotool.utils.CTUtils;


public class CharacterGenerator {
	private static List<Character> upperCaseLetters =  Arrays.asList('A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z');
	private static List<Character> lowerCaseLetters = Arrays.asList('a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z');
	private static List<Character> digits = Arrays.asList('1','2','3','4','5','6','7','8','9','0');
	private static List<Character> symbols = Arrays.asList('!', '?', '$' ,'%', '^', '&' ,'*' ,'_' ,'-' ,'+', '=', '@' ,'#');
	private static List<Character>  ambiguousSymbols = Arrays.asList('{','}','[',']','(',')','/','\\','|','\'','"','`','~',',',';',':','.','<','>');
	private static List<Character>  similarLookingCharacters= Arrays.asList('|','1','I','i','l','L','0','O','o');
	
	private static char getRandomLowerCaseLetter(){
		Random random = new Random();
		int maxLength =lowerCaseLetters.size();
		return lowerCaseLetters.get(random.nextInt(maxLength));		
	}
	
	private static char getRandomUpperCaseLetter(){
		Random random = new Random();
		int maxLength =upperCaseLetters.size();
		return upperCaseLetters.get(random.nextInt(maxLength));		
	}
	
	private static char getRandomDigit(){
		Random random = new Random();
		int maxLength =digits.size();
		return digits.get(random.nextInt(maxLength));		
	}
	
	private static char getRandomSymbol(){
		Random random = new Random();
		int maxLength =symbols.size();
		return symbols.get(random.nextInt(maxLength));		
	}
	
	private static char getRandomAmbiguousSymbol(){
		Random random = new Random();
		int maxLength = ambiguousSymbols.size();
		return ambiguousSymbols.get(random.nextInt(maxLength));
	}
	
	public static char getRandomCharacter() throws Exception{
		
		switch (Characters.getRandomType()) {
		case LOWER_CASE:
			return getRandomLowerCaseLetter();
		case UPPER_CASE:
			return getRandomUpperCaseLetter();
		case DIGITS:
			return getRandomDigit();
		case SYMBOLS:
			return getRandomSymbol();
		case AMBIGUOUS_SYMBOLS:
			return getRandomAmbiguousSymbol();
		default:
			throw new Exception("Exception  generating random character");
		}
	}

	public static char getRandomCharacter(Types ... requiredTypes) throws Exception{
		Random random = new Random();
		int randomPosition = random.nextInt(requiredTypes.length);
		Types randomType= requiredTypes[randomPosition];
		
		switch (randomType) {
		case LOWER_CASE:
			return getRandomLowerCaseLetter();
		case UPPER_CASE:
			return getRandomUpperCaseLetter();
		case DIGITS:
			return getRandomDigit();
		case SYMBOLS:
			return getRandomSymbol();
		case AMBIGUOUS_SYMBOLS:
			return getRandomAmbiguousSymbol();
		default:
			throw new Exception("Exception  generating random character");
		}
	}
	
	public static char getRandomCharacter(Types requireType) throws Exception{
		switch (requireType) {
		case LOWER_CASE:
			return getRandomLowerCaseLetter();
		case UPPER_CASE:
			return getRandomUpperCaseLetter();
		case DIGITS:
			return getRandomDigit();
		case SYMBOLS:
			return getRandomSymbol();
		case AMBIGUOUS_SYMBOLS:
			return getRandomAmbiguousSymbol();
		default:
			throw new Exception("Exception  generating random character");
		}
	}
	
	public static boolean isSimilarLookingCharacter(char character){
		return similarLookingCharacters.contains(character);
	}

	public static char getNonSimilarLookingCharacter() throws Exception {
		char randomChar= getRandomCharacter();
		while(similarLookingCharacters.contains(randomChar)){
			randomChar= getRandomCharacter();
		}
		return randomChar;
	}
	
	public static char getNonSimilarLookingCharacter(Types type) throws Exception {
		char randomChar= getRandomCharacter(type);
		while(similarLookingCharacters.contains(randomChar)){
			randomChar= getRandomCharacter();
		}
		return randomChar;
	}
}
