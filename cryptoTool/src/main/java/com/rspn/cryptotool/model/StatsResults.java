package com.rspn.cryptotool.model;

import com.rspn.cryptotool.utils.CTUtils.EType;

import android.os.Parcel;
import android.os.Parcelable;

public class StatsResults implements Parcelable{
	
	String resultType;
	int[] charCount = new int[25];
	double [] ICforKeyLength = new 	double[25];
	
	public void setResultType(String type){
		resultType=type;
	}
	
	public String getResultType(){
		return resultType;		
	}
	
	public void setCharCount(int[] charCount){
		this.charCount=charCount;
	}
	
	public int[] getCharCount(){
		return charCount;
	}
	
	public void setICforKeyLength(double [] ic){
		this.ICforKeyLength=ic;
	}
	
	public double[] getICforKeyLength(){
		return ICforKeyLength;		
	}
	
	public StatsResults(){
		
	}
	
	public StatsResults(Parcel in){
		resultType=in.readString();
		charCount=in.createIntArray();
		ICforKeyLength=in.createDoubleArray();
		
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(resultType);
		dest.writeIntArray(charCount);
		dest.writeDoubleArray(ICforKeyLength);
		
	}
	
	public static final Parcelable.Creator<StatsResults> CREATOR =
			new Parcelable.Creator<StatsResults>() {

				@Override
				public StatsResults createFromParcel(Parcel source) {
					return new StatsResults(source);
				}

				@Override
				public StatsResults[] newArray(int size) {
					return new StatsResults[size];
				}
				
			};

}
