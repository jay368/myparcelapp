package com.example.myparcelapp.events

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.myparcelapp.utils.ActivityTransferManager.startActivityProductPage

open class ProductPageOpenOnClick(var activity: Activity, var pid: String) : View.OnClickListener{

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View) {
        startActivityProductPage(pid, activity)
    }
}