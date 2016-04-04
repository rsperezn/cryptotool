package com.rspn.cryptotool.model;

public class NavigationItem {

	private String itemName;
	private int imageResourceId;

	public NavigationItem(String itemName, int imageResourceId) {
		super();
		this.itemName = itemName;
		this.imageResourceId = imageResourceId;
	}

	public String getItemName() {
		return itemName;
	}
	public int getImageResourceId() {
		return imageResourceId;
	}


}
