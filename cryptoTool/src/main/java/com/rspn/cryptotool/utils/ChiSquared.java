package com.rspn.cryptotool.utils;

public class ChiSquared {
	
	public static char[] alphabet= CTUtils.getAlphabet();
	public static double calculate(String text){
		double sum=0;
		//format text by removing all spaces special characters and making it upper case
		text= CTUtils.sanitize(text);
		int[] charCount= new int[26];//occurrences of each of the characters
		double[] expected = new double[26];
		//count
		for(int i=0 ; i<text.length();i++){
			charCount[CTUtils.charGetInt(text.charAt(i))]+=1;
		}
		//expected occurrence of each character
		for(int i=0; i<charCount.length;i++ ){
			expected[i]=text.length()*CTUtils.charGetProb(alphabet[i]);
		}
		//calculate
		for(int i=0;i<26;i++){
			sum+=Math.pow((charCount[i]-expected[i]),2)/expected[i];
		}
		return sum;		
	}
	

}
