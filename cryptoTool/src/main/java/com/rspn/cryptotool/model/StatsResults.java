package com.rspn.cryptotool.model;

import android.os.Parcel;
import android.os.Parcelable;

public class StatsResults implements Parcelable{
	
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
	private String resultType;
	private int[] charCount = new int[25];
	private double [] ICforKeyLength = new 	double[25];
	
	public StatsResults(){

	}
	
	public StatsResults(Parcel in){
		resultType=in.readString();
		charCount=in.createIntArray();
		ICforKeyLength=in.createDoubleArray();

	}
	
	public String getResultType(){
		return resultType;
	}
	
	public void setResultType(String type){
		resultType=type;
	}
	
	public int[] getCharCount(){
		return charCount;
	}
	
	public void setCharCount(int[] charCount){
		this.charCount=charCount;
	}
	
	public double[] getICforKeyLength(){
		return ICforKeyLength;
	}

	public void setICforKeyLength(double [] ic){
		this.ICforKeyLength=ic;
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

}
