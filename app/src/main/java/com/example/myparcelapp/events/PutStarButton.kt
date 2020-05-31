package com.example.myparcelapp.events

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import com.example.myparcelapp.R
import kotlinx.android.synthetic.main.alert_putstar.view.*

open class PutStarButton(var activity: Activity, var context: Context, var resources:Resources, var inflater:LayoutInflater, var ip:String, var pid:String, var wb:WebView) : View.OnClickListener {

    override fun onClick(v: View?) {
        val okButtonText = resources.getString(R.string.putStarOkButton)
        val view = inflater.inflate(R.layout.alert_putstar, null)

        val alertDialog = AlertDialog.Builder(context).setPositiveButton(okButtonText){
                    dialog, which ->
                val star = when (true){
                    view.radioButton1.isChecked -> 1
                    view.radioButton2.isChecked -> 2
                    view.radioButton3.isChecked -> 3
                    view.radioButton4.isChecked -> 4
                    view.radioButton5.isChecked -> 5
                    else -> 0
                }.toString()
                Log.d("url :: ", "$ip/starupdate?id=$pid&num=$star")
                wb.loadUrl("$ip/starupdate?id=$pid&num=$star")

            }
            .setNegativeButton(resources.getString(R.string.cancel),null)
            .create()
        alertDialog.setView(view)
        alertDialog.show()
    }
}