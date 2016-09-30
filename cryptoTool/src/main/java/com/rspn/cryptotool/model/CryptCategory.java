package com.rspn.cryptotool.model;

import java.util.List;

public class CryptCategory {
    private String title;
    private List<String> subcategories;


    public CryptCategory(String title, List<String> subcategories) {
        this.title = title;
        this.subcategories = subcategories;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSubcategories() {
        return subcategories;
    }
}
