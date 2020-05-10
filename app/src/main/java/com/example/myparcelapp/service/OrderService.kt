package com.example.myparcelapp.service

import com.example.myparcelapp.dto.OrderVOList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OrderService {
    @GET("/orderlistjson")
    fun orderList(@Query("u") user:String) : Call<OrderVOList>
}