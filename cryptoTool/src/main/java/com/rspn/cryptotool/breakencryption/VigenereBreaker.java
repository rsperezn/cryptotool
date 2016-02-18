package com.rspn.cryptotool.breakencryption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.rspn.cryptotool.utils.CTUtils;
import com.rspn.cryptotool.utils.IC;

public class VigenereBreaker {
	private static Set<Integer> possibleKeysLength = new HashSet<Integer>(); 
	private static List<Integer> tripletsDifference = new ArrayList<Integer>();
	public static int maxKeylength =20;
	private static List<Double>  ICStats=new ArrayList<Double>(maxKeylength);
	static List<String> forcedKEYS= new ArrayList<String>();// all the possible keys generated
	
	public static HashMap<String,Object> runBreak(String encryptedText, boolean whitespaces )
	{
		encryptedText=CTUtils.sanitize(encryptedText);
		initialize();
		int kasikisguess= kasikisTest(encryptedText);
		possibleKeysLength.add(kasikisguess);
		// Now use Friedman Test and see what are the Index of Coincidence that
        //have the closest value to the English IC and then try those sizes of keys
		List<IC> ICS= new ArrayList<IC>(maxKeylength);
        for(int i=0; i< maxKeylength;i++){
            ICS.add(new IC());
        } 
      //create the ArrayList with their poss and value correspondingly
        for(int i=1; i<= maxKeylength;i++){
            ICS.get(i-1).setPos(i);//set just the position
            float currIC=confirmKeyLength(encryptedText, i);
            ICS.get(i-1).setVal(currIC);
            System.out.print("For key length " + i+ ":"+ confirmKeyLength(encryptedText, i));
            ICStats.add((double) currIC);//adding the values for each correspondent ic
            System.out.println();
        }
        Collections.sort(ICS);
      //find the position that is interesting for the IC
        for(int i=0; i<maxKeylength;i++){
            float currIC=ICS.get(i).getVal();
            //just use the number that are useful
            if (currIC>=0.06){
                int position= ICS.get(i).getPos();
                System.out.println("Index: "+position + " value: "+currIC);
                possibleKeysLength.add(position);}
        }
     //generate the actual guesses of the keys
        for(int len :possibleKeysLength){
            String[] splits= splitCiphertext(encryptedText, len);
            String[] guessedKeys=guessKey(splits);
            //cannot add keys that are empty since they will cause an error later when trying to format it
            if(!guessedKeys[0].equals("") && !guessedKeys[0].equals(" ")){
            	forcedKEYS.add(guessedKeys[0]);
            }
            if(!guessedKeys[0].equals("")&& !guessedKeys[1].equals(" ")){
            	forcedKEYS.add(guessedKeys[1]);
            }
        }
        HashMap<String, Object> resultInfo = new HashMap<String,Object>();
        resultInfo.put("forcedKeys", forcedKEYS);
        return resultInfo;
	}
	
	private static void initialize(){
		tripletsDifference.clear();
		possibleKeysLength.clear();
		forcedKEYS.clear();
		ICStats.clear();

	}
	

	private static int kasikisTest(String encryptedText) {
		Set<String> checkedTriplets = new HashSet<String>();
		//read triplets and check if they appear in the rest of the string
		for (int i = 0; i < encryptedText.length()-3; i++) {
			String target= encryptedText.substring(i,i+3);
            String therest =encryptedText.substring(i+3,encryptedText.length());
            if(therest.contains(target)&& (!checkedTriplets.contains(target))){
                getAllTargetPos(encryptedText,target);
                checkedTriplets.add(target);
            }
		}
		//after triplets difference is generated find gcd
        int guess= gcdOfList(tripletsDifference.toArray(new Integer[tripletsDifference.size()]));
		return guess;
	}
	

	private static void getAllTargetPos(String encryptedText, String target){
		List<Integer> repeatedPos= new ArrayList<Integer>();
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(encryptedText);
        int count = 0;
        while (m.find()){
            count +=1;
            System.out.print("Start index: " + (m.start()+1));
            repeatedPos.add(m.start() +1);
            System.out.print(" End index: " + (m.end()+1));
            System.out.println(" Found: " + m.group());
        }
        System.out.println("count:"+count);
        settriplesDiff(repeatedPos);		
	}

	private static void settriplesDiff(List<Integer> repeatedPos) {
		int size=repeatedPos.size();
        int firsthalf=size-1 ;//the initial round
        int dist= size-2;
        int secondhalf=(dist*(dist+1))/2;//second half
        int total= firsthalf+secondhalf;
        int[] result= new int[total];
        //filling in the first half
        for(int i=0;i<size-1;i++){
                result[i]= repeatedPos.get(i+1)-repeatedPos.get(i);
                tripletsDifference.add(result[i]);
        }
        //filling in the second half
        if(total>=3){
           int pos=2;
           int index=firsthalf;
           for(int i=0;i<dist;i++){
               for(int j =0; j<pos-1;j++){
                   result[index]= repeatedPos.get(pos)-repeatedPos.get(j);
                   tripletsDifference.add(result[index]);
                   index++;
               }
               pos++;
           }
        }
	}
	
	// loop though list of distances and get the gcd
	private static int gcdOfList(Integer[] intArray) {
		int size=  intArray.length;
        if(size<2){
            return 0;
        }
        int firstgcd= gcd(intArray[0],intArray[1]);
        //recursively find gdc with the already found first gcd
        for(int i=0;i<size;i++){
            firstgcd= gcd(firstgcd,intArray[i]);
        }
        return firstgcd;
		
	}
	
	//recursively find the gcd of two numbers
    public static int gcd (int a, int b){
        if(b==0){
            return a;
        }
        return gcd(b,a%b);
    }    
	
    public static float confirmKeyLength(String encryptedText, int lengthguess){
        String[] splits=splitCiphertext(encryptedText, lengthguess);
        float[] ICs= new float[lengthguess];//same number as lengthguess
        //calculate the IC of each substring
        float currSum=0;
        for(int i=0; i< lengthguess;i++){
        	ICs[i]=(Float) IC.calculte(splits[i]).get("IC");
        	currSum+=ICs[i];
        }
        //average IC of the whole splitted version of the original string
        return (float)currSum/lengthguess;
    }
	
    /*split the string in the corresponding substring such as 
     *  0,4,8….. n-4 for the first part 1,5,9.. n-5
     *  if we guess the lenght is 4
     * */
    public static String[] splitCiphertext(String ciphertext, int klengthguess){
        String[] subStrings = new String[klengthguess];
        //initialize empty groups of stings
        for(int k=0;k<klengthguess;k++){
            subStrings[k]="";  
        }
        //create the corresponding substrings
        for(int i=0;i<klengthguess;i++){
            for(int j=0;j<ciphertext.length()-klengthguess;j=j+klengthguess){
                subStrings[i]=subStrings[i].concat(Character.toString(ciphertext.charAt(i+j)));           
            }
        }
        return subStrings;
    }

    private static String[] guessKey(String splits[]){
        //for each of the substrings shift them and calculate the chisquare
        int totsubstring=splits.length;
        String theguess1="";
        String theguess2="";
        int[] keypos=new int[totsubstring*2];
        String currShift="";
        double[]buffer=new double[26];
        for(int i=0;i<totsubstring;i++){//for each substring
            for(int j=0;j<26;j++){//do 26 times
                for(int k=0;k<splits[i].length();k++){//for all the ciphertext
                    char curr=splits[i].charAt(k);
                    char prevchar= prevChar(curr,j);
                    currShift=currShift.concat(Character.toString(prevchar));
                }
                buffer[j]=Chisqr(currShift);
                currShift="";//restart and then reuse it */
            }
            keypos[i*2]=getMinpos(buffer)[0];//flush the buffer to the ArrayList
            keypos[i*2+1]=getMinpos(buffer)[1];
            buffer= new double[26];//empty the buffer
        }
        //since it may be possible that the results of Chisquare give some 
        //erroneous shifts to be made to the key. Get the 2 smallest values
        
        for(int k =0; k<keypos.length/2;k++){
            theguess1=theguess1.concat(Character.toString(CTUtils.getChar(keypos[k*2])));
            theguess2=theguess2.concat(Character.toString(CTUtils.getChar(keypos[2*k+1])));
        }
        System.out.println("The guessed key1: "+ theguess1);
        System.out.println("The guessed key2: "+ theguess2);
        
        String[] theGuesses= new String[2];
        theGuesses[0]=theguess1;
        theGuesses[1]=theguess2;
        return theGuesses;
      
    }
    
    private static char prevChar(char curr,int disp){
        int currint= CTUtils.charGetInt(curr);
        int prevint= ((currint-disp)+26)%26;
        return CTUtils.getChar(prevint);
    }
    
  //calculate the chisquare of a given string.
    //later we will have to find the mininum of every shift
    private static double Chisqr(String currShift){
        int[] charCount=new int [26];
        for(int i =0; i<currShift.length();i++){
            charCount[CTUtils.charGetInt((currShift.charAt(i)))]++;
        }
        double sum=0;
        int length=currShift.length();
        for(int j =0;j<26;j++){
            sum= sum +Math.pow((charCount[j]- length*CTUtils.getProb(j)), 2)/(double)(length*CTUtils.getProb(j));
        }  
        return sum;
    }
    
    //gets the possition of the first character that must for the
    //keyword for th ciphertext
    private  static int[] getMinpos(double[] chisqr){
        int min1=0;int min2=0; //since we care about the possition that'll require an alphabet with >100chars
        for(int i=0; i<chisqr.length;i++){
            for(int j =0; j<26;j++){
                if(chisqr[j] < chisqr[min1]) {
                    min1 = j;
                }
                
            }
            for(int k =0; k<26;k++){
                if(chisqr[k] < chisqr[min2] && (k!=min1)) {
                    min2 = k;
                }    
            }
        }
        int[]result={min1,min2};
        return result;
    }
    
    public static double[] getICs(){
    	double [] result = new double[ICStats.size()];
    	for (int i=0;i<ICStats.size();i++){
    		result[i]=ICStats.get(i);    		
    	}
    	return result;
    }
}
