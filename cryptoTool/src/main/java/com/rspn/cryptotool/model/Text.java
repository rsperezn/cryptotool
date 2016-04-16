package com.rspn.cryptotool.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.rspn.cryptotool.utils.CTUtils;

public class Text implements Parcelable {
    public static final Parcelable.Creator<Text> CREATOR =
            new Parcelable.Creator<Text>() {

                @Override
                public Text createFromParcel(Parcel source) {
                    Log.i(CTUtils.TAG, "createFromParcel");
                    return new Text(source);
                }

                @Override
                public Text[] newArray(int size) {
                    Log.i(CTUtils.TAG, "newArray");
                    return new Text[size];
                }

            };
    private long id;
    private String title;
    private String type;
    private String content;
    private int deletable;

    public Text() {
    }

    //order is really important
    //This constructor is invoked by the method createFromParcel(Parcel source) of the object CREATOR
    public Text(Parcel in) {
        Log.i(CTUtils.TAG, "Parcel constructor");
        id = in.readLong();
        title = in.readString();
        type = in.readString();
        content = in.readString();
        deletable = in.readInt();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDeletable() {
        return deletable;
    }

    public void setDeletable(int deletable) {
        this.deletable = deletable;
    }

    //this is going to be call by the arrayAdapter
    public String toString() {
        return title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(content);
        dest.writeInt(deletable);
    }
}
