package com.rspn.cryptotool.utils;

public enum TextTypes {
            ET{
                @Override
                public String toString() {
                    return "EncryptedText";
                }
            },PT{
                @Override
                public String toString() {
                    return "PlainText";
                }
            },DT{
                @Override
                public String toString() {
                    return "DecryptedText";
                }
            },BET{
                @Override
                public String toString() {
                    return "BrokenEncryptionText";
                }
            },
            ;
}
