package com.example.myparcelapp.utils

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.myparcelapp.*
import com.example.myparcelapp.view.*

object ActivityTransferManager {

    private fun startActivity(context: Context?, intent: Intent?, isPush: Boolean = false) {

        val mContext = context ?: MyApplication.getAppContext()

        intent?.let {
            if(isPush) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(intent)
        }

    }

    private fun startActivity(activity: Activity, intent: Intent?, isPush: Boolean = false) {

        intent?.let {
            if(isPush) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
        }

    }



    private fun startHomeActivity(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startActivityByBottomTabClick(activity: Activity, currentTabId: Int, selectedTabId: Int): Boolean {

        Log.d("TEST", "startActivityByBottomTabClick in")

        if(currentTabId == selectedTabId) return false

        var intent: Intent? = null

        when(selectedTabId){
            R.id.category -> intent = Intent(activity, CategoryActivity::class.java)
            R.id.search -> intent = Intent(activity, SearchActivity::class.java)
            R.id.home -> {
                startHomeActivity(activity)
                return true
            }
            R.id.basket -> intent = Intent(activity, BasketActivity::class.java)
            R.id.order -> intent = Intent(activity, Order_Activity::class.java)
        }

        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            activity.startActivity(it , ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }

        return true
    }

}