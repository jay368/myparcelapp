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
            R.id.order -> intent = Intent(activity, OrderActivity::class.java)
        }

        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            activity.startActivity(it , ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
        }

        return true
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startActivityBuyButtonClick(activity: Activity,
                                    buyIndexlist:ArrayList<String>,
                                    buyNumlist:ArrayList<Int>,
                                    buyTitlelist:ArrayList<String>,
                                    buyImagelist:ArrayList<String>,
                                    BuyingProduct_total:Int) {
        var intent: Intent? = null
        intent = Intent(activity, BuyActivity::class.java)
        intent.putExtra("buyIndexlist", buyIndexlist)
        intent.putExtra("buyNumlist", buyNumlist)
        intent.putExtra("buyTitlelist", buyTitlelist)
        intent.putExtra("buyImagelist", buyImagelist)
        intent.putExtra("total", BuyingProduct_total)
        activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
    }//구매 버튼

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun startActivityProductPage(index:String, activity: Activity){
        val intent = Intent(activity, ProductActivity::class.java)
        intent.putExtra("pid", index)
        activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
    }//상품 페이지로 이동

}