package com.rspn.cryptotool.encrypt;


import java.util.Hashtable;

import com.rspn.cryptotool.utils.CTUtils;

public class VigenereEncryption {
	private static char[] alphabet= CTUtils.getAlphabet();
	private  static Hashtable<Character,Integer> charToint = new Hashtable<Character,Integer>();
	public static char[]cypherText;
	private static boolean initialized =false;
	private static boolean retainCase;
	public static String plainText;

	public static void initComponents(){
		if(!initialized){
			//for upper case characters
			for(int i=0;i<alphabet.length/2;i++){
				charToint.put(alphabet[i], i);
				}
			//for lower case with the same upper case equivalent
			for (int i=26;i<alphabet.length;i++){
				charToint.put(alphabet[i],i%26);
			}
			initialized=true;
		}	
	}

	public static String runEncryption(String plainText,String keyword,boolean whitespaces){
		int length= plainText.length();
		String fmtKeyword= CTUtils.formatKeyword(length, keyword);
		cypherText= new char[length];
		retainCase=CTUtils.retainCase;
		//TODO implement on sharedpreferences retainCase
		if(!retainCase){
			plainText=plainText.toUpperCase();
			fmtKeyword= fmtKeyword.toUpperCase();
		}
		int keypos=0;
		for(int i =0; i<length;i++){
			char currPlainChar= plainText.charAt(i);//current character of the plaintext
			boolean upper= Character.isUpperCase(currPlainChar);
			char currKeywordChar= fmtKeyword.charAt(keypos);//current character in the keyword that now matches the length of the plaintext
			if (charToint.containsKey(currPlainChar)){//if it's alphabetic character
				int plain=(charToint.get(currPlainChar));
				int key=(charToint.get(currKeywordChar));
				int cipher= (plain+key)%26;
				char encryptedChar= alphabet[cipher];
				if(retainCase)//if we need to retain the case then convert it to the case plaintext's character
					encryptedChar= (upper) ? Character.toUpperCase(encryptedChar) :Character.toLowerCase(encryptedChar);
				cypherText[i]= encryptedChar;
				keypos++;
			}

			else if(currPlainChar=='\n'){
				cypherText[i]='\n';      	
			}
			else{//all other non alpha character will remain unchanged
				cypherText[i]=currPlainChar;
			}
		}
		if(whitespaces)
			return new String(cypherText);
		else
			return new String(cypherText).replaceAll(" ", "");
		
	}

	


}
