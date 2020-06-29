package com.example.myparcelapp.model

import com.google.gson.annotations.SerializedName
data class MobileLoginVOList
    (
    @SerializedName("ml") var ml: List<MobileLoginVO>
    )
{}