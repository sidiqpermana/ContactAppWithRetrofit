package com.nbs.ppm.contactapp.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Sidiq on 18/06/2015.
 */
public class Contact extends Observable implements Serializable {

    @SerializedName("command")
    public String command;

    @SerializedName("status")
    public String status;

    @SerializedName("contacts")
    public ArrayList<ItemContact> listContact = new ArrayList<>();

    Contact contact;
    int position;

    public static String NEED_TO_REFRESH = "needToRefresh";
    public static String NEED_TO_DELETE = "needToDelete";

    public void onItemChanged(){
        setChanged();
        notifyObservers(NEED_TO_REFRESH);
    }

    public void onDeletedItem(){
        setChanged();
        notifyObservers(NEED_TO_DELETE);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public class ItemContact implements Serializable{
        @SerializedName("id")
        public String id;
        @SerializedName("name")
        public String name;
        @SerializedName("email")
        public String email;
        @SerializedName("mobile")
        public String phone;
    }
}
