package com.example.stayathome.server;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface JsonTreeApi {

    @POST
    Call<RealTree>creratePost(@Body RealTree realTree);
}
