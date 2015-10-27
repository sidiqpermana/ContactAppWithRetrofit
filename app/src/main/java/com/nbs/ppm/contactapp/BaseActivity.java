package com.nbs.ppm.contactapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nbs.ppm.contactapp.api.ApiService;
import com.nbs.ppm.contactapp.api.ServiceGenerator;

/**
 * Created by Sidiq on 28/10/2015.
 */
public class BaseActivity extends AppCompatActivity {
    public ApiService apiService = null;
    public String REQUEST_SUCCESS = "Success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ServiceGenerator.createService(ApiService.class);
    }
}
