package com.example.myparcelapp.model

import com.google.gson.annotations.SerializedName

data class OrderVOList (
    @SerializedName("ol") var ol: List<OrderVO>

)
{}