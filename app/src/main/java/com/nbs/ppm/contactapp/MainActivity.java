package com.nbs.ppm.contactapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nbs.ppm.contactapp.adapter.ContactAdapter;
import com.nbs.ppm.contactapp.model.Contact;
import com.nbs.ppm.contactapp.util.AppUrl;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class MainActivity extends BaseActivity implements Observer {

    private ContactAppApplication application;

    private RecyclerView rvItem;
    private ProgressBar indicator;
    private Toolbar toolbar;
    private FloatingActionButton fabAdd;

    private ArrayList<Contact.ItemContact> listItem;
    private ContactAdapter adapter;

    private String url = AppUrl.getUrl(AppUrl.ApiAction.VIEW);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        rvItem = (RecyclerView)findViewById(R.id.rv_item);
        rvItem.setHasFixedSize(true);

        LinearLayoutManager lnLayoutManager = new LinearLayoutManager(MainActivity.this);
        rvItem.setLayoutManager(lnLayoutManager);

        indicator = (ProgressBar)findViewById(R.id.pb_progress);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);

        application = (ContactAppApplication)getApplication();
        application.getItemContact().addObserver(this);

        listItem = new ArrayList<>();

        viewRequest();

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FormCreateUpdateActivity.toFormCreateUpdateActivity(MainActivity.this);
            }
        });

    }

    private void viewRequest() {
        rvItem.setVisibility(View.GONE);
        indicator.setVisibility(View.VISIBLE);

        if (listItem.size() > 0){
            listItem.clear();
        }

        Call<Contact> call = apiService.getContact();
        call.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Response<Contact> response, Retrofit retrofit) {
                rvItem.setVisibility(View.VISIBLE);
                indicator.setVisibility(View.GONE);

                Contact contact = response.body();
                if (contact != null){
                    if (contact.status.equals(REQUEST_SUCCESS)){
                        listItem = contact.listContact;
                        adapter = new ContactAdapter(MainActivity.this, listItem);
                        rvItem.setAdapter(adapter);
                    }else{
                        Toast.makeText(MainActivity.this, "Failed to get data", Toast.LENGTH_LONG).show();;
                    }

                }
            }

            @Override
            public void onFailure(Throwable t) {
                rvItem.setVisibility(View.GONE);
                indicator.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, t != null ? t.getMessage() : "Failed to get data", Toast.LENGTH_LONG).show();;
            }
        });

    }


    @Override
    public void update(Observable observable, Object o) {
        if (o.equals(Contact.NEED_TO_REFRESH)){
            viewRequest();
        }

        if (o.equals(Contact.NEED_TO_DELETE)){
            int i = application.getItemContact().getPosition();
            listItem.remove(i);
            adapter.notifyItemRemoved(i);
        }

    }
}
