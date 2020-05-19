package com.example.myparcelapp.service

import com.example.myparcelapp.dto.ProductVOList
import com.example.myparcelapp.dto.UserVO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {
    @GET("/user_json")
    fun user(@Query("ui") user:String) : Call<UserVO>
}