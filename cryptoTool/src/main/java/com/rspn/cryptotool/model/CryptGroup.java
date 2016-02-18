package com.rspn.cryptotool.model;

import java.util.ArrayList;
import java.util.List;

public class CryptGroup {

    public String string;
    public final List<String> children = new ArrayList<>();

    public CryptGroup(String string) {
        this.string = string;
    }

}
