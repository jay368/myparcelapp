package com.example.myparcelapp

import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.SearchView
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class SearchButton : SearchView.OnQueryTextListener {
    var any:String
    var activity:Activity
    var context: Context
    var flt:String
    var st:String
    var tag:String
    var br:String
    var agn:String

    constructor(activity:Activity, context:Context, flt:String, any:String){
        this.activity=activity
        this.context=context
        this.flt=flt
        this.st="0"
        this.tag=""
        this.br=""
        this.agn="0"
        this.any=any
    }

    fun SetOptions_flt(flt:String){
        this.flt=flt
    }
    fun SetOptions_st(st:String){
        this.st=st
    }
    fun SetOptions_tag(tag:String){
        this.tag=tag
    }
    fun SetOptions_br(br:String){
        this.br=br
    }
    fun SetOptions_agn(agn:String){
        this.agn=agn
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onQueryTextSubmit(query: String): Boolean {
       if(activity.javaClass == SearchresultActivity::class.java){
           var tag_temp =tag
           var br_temp =br
           if (tag_temp == any){
               tag_temp = ""
           }
           if (br_temp == any){
               br_temp = ""
           }
           (activity as SearchresultActivity).SearchResultListInitialize(query,flt,st,tag_temp,br_temp,agn,activity)
       }
        else{
           (activity as SearchActivity).StartActivitySearchresultActivity(query, flt)
       }
       return true

    }
    //텍스트가 바뀔때마다 호출
    override fun onQueryTextChange(newText: String): Boolean {
        return true
    }
}