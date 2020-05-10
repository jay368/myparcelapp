package com.example.myparcelapp

import android.app.Activity
import android.content.Context
import android.view.View
import com.example.myparcelapp.dto.ProductVOList
import retrofit2.Callback

open class ProductPageOpenOnClick : View.OnClickListener{
    var pid:String
    var activity: Activity
    var context:Context
    constructor(activity:Activity, context:Context, pid:String){
        this.pid=pid
        this.activity=activity
        this.context = context
    }


    override fun onClick(v: View) {
        if(activity.javaClass == MainActivity::class.java){
            (activity as MainActivity).ProductPageOpen(pid)
        }
        else if(activity.javaClass == SearchresultActivity::class.java){
            (activity as SearchresultActivity).ProductPageOpen(pid)
        }
        else if(activity.javaClass == ProductActivity::class.java){
            (activity as ProductActivity).AnotherProduct(pid)
        }
    }
}