package com.rspn.cryptotool.breakencryption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.rspn.cryptotool.decrypt.CaesarsDecryption;
import com.rspn.cryptotool.utils.ChiSquared;

public class CaesarBreaker {
	
	public static HashMap<String,Object>  runBreak(String encryptedText, boolean whitespaces){
		HashMap<Double,Integer> results= new HashMap<Double,Integer>();
		String tempDecrypt;
		double tempFitness;
		for (int i = 0; i < 26; i++) {
			tempDecrypt= CaesarsDecryption.runDecryption(encryptedText, i, whitespaces);
			tempFitness= ChiSquared.calculate(tempDecrypt);
			results.put(tempFitness,i);
		}
		//find best fit and decrypt with such value
		double minFitness=Collections.min(results.keySet());
		int shiftVal= results.get(minFitness);
		HashMap<String,Object>resultsInfo= new HashMap<String,Object>(); //shiftval and actual decrypted text
		resultsInfo.put("shift",shiftVal);
		resultsInfo.put("decryptedText",CaesarsDecryption.runDecryption(encryptedText, shiftVal, whitespaces));
		return resultsInfo;
	}

}
