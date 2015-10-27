package com.nbs.ppm.contactapp;

import android.app.Application;

import com.nbs.ppm.contactapp.model.Contact;

/**
 * Created by Sidiq on 18/06/2015.
 */
public class ContactAppApplication extends Application {
    Contact itemContact;

    @Override
    public void onCreate() {
        super.onCreate();
        itemContact = new Contact();
    }

    public Contact getItemContact(){
        return  itemContact;
    }
}
