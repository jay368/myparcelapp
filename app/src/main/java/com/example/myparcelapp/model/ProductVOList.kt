package com.example.myparcelapp.model

import com.google.gson.annotations.SerializedName
data class ProductVOList
    (
    @SerializedName("prdlist") var prdlist: List<ProductVO>,
    @SerializedName("tag") var tag: List<String>,
    @SerializedName("brand") var brand: List<String>,
    @SerializedName("pdimages") var pdimages: List<String>,

    @SerializedName("prdlist_sametag") var prdlist_sametag: List<ProductVO>,
    @SerializedName("prdlist_samebrand") var prdlist_samebrand: List<ProductVO>
    )
{}