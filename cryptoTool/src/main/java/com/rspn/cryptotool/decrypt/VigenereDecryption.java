package com.rspn.cryptotool.decrypt;

import java.util.HashSet;
import java.util.Hashtable;

import com.rspn.cryptotool.utils.CTUtils;

public class VigenereDecryption {
	private static char[] alphabet= CTUtils.getAlphabet();
	private  static Hashtable<Character,Integer> charToint = new Hashtable<Character,Integer>();
	public static char[]plainText;
	private static boolean initialized =false;
	public static String cypherText;
	private static boolean retainCase;

	public static void initComponents(){
		if(!initialized){
			for(int i=0;i<alphabet.length/2;i++){
				charToint.put(alphabet[i], i);
			}
			for (int i = 26; i < alphabet.length; i++) {
				charToint.put(alphabet[i],i%26);
			}
			initialized=true;
		}	
	}
	
	public static String runDecryption(String cypherText, String keyword,boolean whitespaces){
		int length= cypherText.length();
		String fmtKeyword= CTUtils.formatKeyword(length, keyword);
		plainText= new char[length];
		retainCase=CTUtils.retainCase;
		if(!retainCase){
			cypherText=cypherText.toUpperCase();
			fmtKeyword= fmtKeyword.toUpperCase();
		}
		
		int keypos=0;
		for(int i =0; i<length;i++){
			char currCypherChar= cypherText.charAt(i);//current character of the plaintext
			boolean upper= Character.isUpperCase(currCypherChar);
			char currKeywordChar= fmtKeyword.charAt(keypos);//current character in the keyword that now matches the length of the plaintext
			if (charToint.containsKey(currCypherChar)){//if it's alphabetic character
				int cypher=(charToint.get(currCypherChar));
				int key=(charToint.get(currKeywordChar));
				int plain= (((cypher-key)%26)+26)%26;//to get a positive value of java modulus
				char decryptedChar= alphabet[plain];
				if(retainCase)//if we need to retain the case then convert it to the case plaintext's character
					decryptedChar= (upper) ? Character.toUpperCase(decryptedChar) :Character.toLowerCase(decryptedChar);
				plainText[i]= decryptedChar;
				keypos++;
			}

			else if(currCypherChar=='\n'){
				plainText[i]='\n';      	
			}
			else{//all other non alphacharacter will remain unchanged
				plainText[i]=currCypherChar;
			}
		}
		if(whitespaces)
			return new String(plainText);
		else
			return new String(plainText).replaceAll(" ", "");
	
	
	}

		
}
