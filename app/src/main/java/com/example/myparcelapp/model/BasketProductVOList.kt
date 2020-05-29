package com.example.myparcelapp.model

import com.google.gson.annotations.SerializedName

data class BasketProductVOList
    (
    @SerializedName("bpl") var bpl: List<BasketProductVO>,
    @SerializedName("total") var total: String
    )
{}