package com.nbs.ppm.contactapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nbs.ppm.contactapp.model.Contact;

import java.util.Observable;
import java.util.Observer;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class FormCreateUpdateActivity extends BaseActivity implements Observer{

    private EditText edtName, edtEmail, edtPhone;
    private Button btnSave;
    private Toolbar toolbar;

    private String url = null;
    public static String KEY_ITEM = "item";
    public static String KEY_POSITION = "position";
    private Contact.ItemContact itemContact = null;
    private int position;
    private boolean isUpdateForm = false;

    private ContactAppApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_create_update);

        edtEmail = (EditText)findViewById(R.id.edt_email);
        edtName = (EditText)findViewById(R.id.edt_name);
        edtPhone = (EditText)findViewById(R.id.edt_phone);
        btnSave = (Button)findViewById(R.id.btn_save);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null){
            itemContact = (Contact.ItemContact) savedInstanceState.getSerializable(KEY_ITEM);
            position = savedInstanceState.getInt(KEY_POSITION);
        }else{
            itemContact = (Contact.ItemContact) getIntent().getSerializableExtra(KEY_ITEM);
            position = getIntent().getIntExtra(KEY_POSITION, 0);
        }

        application = (ContactAppApplication)getApplication();
        application.getItemContact().addObserver(this);

        String actionBarTitle = null;
        if (itemContact != null){
            isUpdateForm = true;
            actionBarTitle = "Update Contact";

            edtPhone.setText(itemContact.phone);
            edtName.setText(itemContact.name);
            edtEmail.setText(itemContact.email);
        }else{
            actionBarTitle = "Create Contact";
        }

        getSupportActionBar().setTitle(actionBarTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();

                if (name.equals("") || email.equals("") || phone.equals("")){
                    Toast.makeText(FormCreateUpdateActivity.this, "All fields are mandatory", Toast.LENGTH_LONG).show();
                }else{
                    String contactId = itemContact == null ? null : itemContact.id;
                    postRequest(name, email, phone, contactId);
                }
            }
        });
    }

    private void postRequest(String name, String email, String phone, String id) {
        final ProgressDialog dialog = new ProgressDialog(FormCreateUpdateActivity.this);
        dialog.setMessage("Please wait...");
        dialog.show();
        Call<Contact> call = null;
        if (!isUpdateForm){
            call = apiService.postContact(name, email, phone);
            call.enqueue(new Callback<Contact>() {
                @Override
                public void onResponse(Response<Contact> response, Retrofit retrofit) {
                    dialog.dismiss();
                    Contact contact = response.body();
                    if (contact.status.equals(REQUEST_SUCCESS)){
                        application.getItemContact().onItemChanged();
                        Toast.makeText(FormCreateUpdateActivity.this, "Success to post data", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(FormCreateUpdateActivity.this, "Failed to update data", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(FormCreateUpdateActivity.this, t != null ? t.getMessage() : "Failed to post data", Toast.LENGTH_LONG).show();
                }
            });
        }else{
            call = apiService.updateContact(name, email, phone, id);
            call.enqueue(new Callback<Contact>() {
                @Override
                public void onResponse(Response<Contact> response, Retrofit retrofit) {
                    dialog.dismiss();
                    Contact contact = response.body();
                    if (contact.status.equals(REQUEST_SUCCESS)) {
                        application.getItemContact().onItemChanged();
                        Toast.makeText(FormCreateUpdateActivity.this, "Success to update data", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        Toast.makeText(FormCreateUpdateActivity.this, "Failed to update data", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(FormCreateUpdateActivity.this, t != null ? t.getMessage() : "Failed to update data", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static void toFormCreateUpdateActivity(Activity activity){
        Intent intent = new Intent(activity, FormCreateUpdateActivity.class);
        activity.startActivity(intent);
    }

    public static void toFormCreateUpdateActivity(Activity activity, Contact.ItemContact itemContact, int position){
        Intent intent = new Intent(activity, FormCreateUpdateActivity.class);
        intent.putExtra(KEY_ITEM, itemContact);
        intent.putExtra(KEY_POSITION, position);
        activity.startActivityForResult(intent, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form_create_update, menu);
        MenuItem item = menu.findItem(R.id.action_delete);
        if (!isUpdateForm){
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            if (isUpdateForm){
                showDeleteAlert(itemContact);
            }
            return true;
        }

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable observable, Object o) {

    }

    public void showDeleteAlert(final Contact.ItemContact itemContact){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FormCreateUpdateActivity.this);
        alertDialogBuilder.setTitle("Contact App");
        alertDialogBuilder
                .setMessage("Are you sure want to delete this item?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        deleteRequest(itemContact);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void deleteRequest(final Contact.ItemContact item) {
        final ProgressDialog dialog = new ProgressDialog(FormCreateUpdateActivity.this);
        dialog.setTitle("Delete Item");
        dialog.setMessage("Please wait...");
        dialog.show();

        Call<Contact> call = apiService.deleteContact(itemContact.id);
        call.enqueue(new Callback<Contact>() {
            @Override
            public void onResponse(Response<Contact> response, Retrofit retrofit) {
                Contact contact = response.body();
                if (contact.status.equals("Success")){
                    application.getItemContact().setPosition(position);
                    application.getItemContact().onDeletedItem();
                    Toast.makeText(FormCreateUpdateActivity.this, "Success to delete data", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(FormCreateUpdateActivity.this, "Failed to delete data", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(FormCreateUpdateActivity.this, t != null ? t.getMessage() : "Failed to delete data", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY_ITEM, itemContact);
        outState.putSerializable(KEY_POSITION, position);
        super.onSaveInstanceState(outState);
    }
}
