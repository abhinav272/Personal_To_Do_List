package com.abhinav.personalto_dolist.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by abhinavsharma on 27-02-2016.
 */
public class ToDoItem implements Parcelable {
    private String item_name;
    private String item_date;
    private String item_time;
    private String item_location;
    private String item_notification;

    public static class List extends ArrayList<ToDoItem>{}

    protected ToDoItem(Parcel in) {
        item_name = in.readString();
        item_date = in.readString();
        item_time = in.readString();
        item_location = in.readString();
        item_notification = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(item_name);
        dest.writeString(item_date);
        dest.writeString(item_time);
        dest.writeString(item_location);
        dest.writeString(item_notification);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ToDoItem> CREATOR = new Creator<ToDoItem>() {
        @Override
        public ToDoItem createFromParcel(Parcel in) {
            return new ToDoItem(in);
        }

        @Override
        public ToDoItem[] newArray(int size) {
            return new ToDoItem[size];
        }
    };

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_date() {
        return item_date;
    }

    public void setItem_date(String item_date) {
        this.item_date = item_date;
    }

    public String getItem_time() {
        return item_time;
    }

    public void setItem_time(String item_time) {
        this.item_time = item_time;
    }

    public String getItem_location() {
        return item_location;
    }

    public void setItem_location(String item_location) {
        this.item_location = item_location;
    }

    public String getItem_notification() {
        return item_notification;
    }

    public void setItem_notification(String item_notification) {
        this.item_notification = item_notification;
    }

    public ToDoItem() {
    }

}
