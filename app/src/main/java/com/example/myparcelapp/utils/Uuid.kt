package com.example.myparcelapp.utils

import android.content.Context
import android.provider.Settings

object Uuid {
    lateinit var userIndex:String
    fun getUuid(context:Context) : String {
        return android.provider.Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}
