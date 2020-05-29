package com.example.myparcelapp.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class OrderVO (
    var index: String,
    var user: String,
    var shipping_place:String,
    var day: Date,
    @SerializedName("prds") var prds: List<OrderProductsVO>,
    var total:Int
)