package com.example.myparcelapp.service

import com.example.myparcelapp.model.ProductVOList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductPageService {
    @GET("/productpage_json")
    fun productList(@Query("pid") pid:String,
                    @Query("u") user:String) : Call<ProductVOList>
}