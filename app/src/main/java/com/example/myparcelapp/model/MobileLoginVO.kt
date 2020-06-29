package com.example.myparcelapp.model

import com.google.gson.annotations.SerializedName

data class MobileLoginVO
    (
    @SerializedName("index") var index: String,
    @SerializedName("uuid") var uuid: String
)