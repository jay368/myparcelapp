package com.example.myparcelapp.service

import com.example.myparcelapp.model.BasketProductVOList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BasketProductService {
    @GET("/basketlist_json")
    fun basketProductList(@Query("u") user:String) : Call<BasketProductVOList>
}
