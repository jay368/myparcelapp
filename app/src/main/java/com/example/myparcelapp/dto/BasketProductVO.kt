package com.example.myparcelapp.dto

import retrofit2.http.GET
import java.util.*


data class BasketProductVO (
    var user: String,
    var product_index: String,
    var day: Date,
    var num: Int,
    var checked: Int = 0,
    var name: String,
    var category: String,
    var pay: Int,
    var img: String
    )