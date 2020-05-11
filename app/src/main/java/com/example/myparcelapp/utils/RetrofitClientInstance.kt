package com.example.myparcelapp.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientInstance {
    private var retrofit:Retrofit? = null

    val retrofitInstance: Retrofit?
    get(){
        retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.55.231:8080/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        return retrofit
    }

}