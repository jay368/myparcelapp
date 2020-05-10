package com.example.myparcelapp.dto

import com.google.gson.annotations.SerializedName

data class OrderVOList (
    @SerializedName("ol") var ol: List<OrderVO>

)
{}