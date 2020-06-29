package com.example.myparcelapp.service

import com.example.myparcelapp.model.BasketProductVOList
import com.example.myparcelapp.model.MobileLoginVOList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MobileLoginService {
    @GET("/mobilelogin_json")
    fun mobileLoginList(@Query("uuid") user:String) : Call<MobileLoginVOList>
}