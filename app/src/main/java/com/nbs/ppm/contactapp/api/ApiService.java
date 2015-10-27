package com.nbs.ppm.contactapp.api;

import com.nbs.ppm.contactapp.model.Contact;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by Sidiq on 28/10/2015.
 */
public interface ApiService {
    @GET("index.php?k=test&c=1")
    Call<Contact> getContact();

    @FormUrlEncoded
    @POST("index.php?k=test&c=2")
    Call<Contact> postContact(@Field("name") String name, @Field("email") String email,
                              @Field("mobile") String phone);

    @FormUrlEncoded
    @POST("index.php?k=test&c=3")
    Call<Contact> updateContact(@Field("name") String name, @Field("email") String email, @Field("mobile") String phone,
                                @Field("id") String id);

    @FormUrlEncoded
    @POST("index.php?k=test&c=4")
    Call<Contact> deleteContact(@Field("id") String id);
}
