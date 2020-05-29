package com.example.myparcelapp.model

import java.util.*

data class ProductVO (
    var index: String,
    var name: String,
    var pay: String,
    var explanatory: String,
    var by: String,
    var kind: String,
    var day: Date,
    var extra :String,
    var tag: List<String>,
    var brand: String,
    var img:String,
    var star:Int,
    var basketed:Int)