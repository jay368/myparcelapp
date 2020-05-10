package com.example.myparcelapp

import android.app.Activity
import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference

class MyApplication: Application() {

    companion object {


        var currentActivity: WeakReference<Activity>? = null
        fun getAppActivity(): Activity? = currentActivity?.get()
        fun setAppActivity(activity: Activity?) {
            currentActivity = if(activity != null) WeakReference(activity) else null
        }


        lateinit var mContext: WeakReference<Context>
        lateinit var noneNullContext: Context
        fun getAppContext(): Context = mContext.get() ?: noneNullContext

    }



    override fun onCreate() {
        super.onCreate()
    }
}