package com.example.myparcelapp.events

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.example.myparcelapp.utils.ActivityTransferManager.startActivityProductPage

open class ProductPageOpenOnClick : View.OnClickListener{
    var pid:String
    var activity: Activity
    var context:Context
    constructor(activity: Activity, context:Context, pid:String){
        this.pid=pid
        this.activity=activity
        this.context = context
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(v: View) {
        startActivityProductPage(pid, activity)
    }
}