package com.rspn.cryptotool.calculatehashes;

import java.util.ArrayList;
import java.util.List;

public class HashData {
	private String algorithm;
	private String hashType;
	private ArrayList<String> data;
	
	
	public HashData(String algorithm, String hashType, ArrayList<String> data) {
		this.algorithm = algorithm;
		this.hashType = hashType;
		this.data = data;
	}
	public String getAlgorithm() {
		return algorithm;
	}

	public String getHashType() {
		return hashType;
	}

	public ArrayList<String> getData() {
		return data;
	}
	public void setData(ArrayList<String> data) {
		this.data = data;
	}
	

}
