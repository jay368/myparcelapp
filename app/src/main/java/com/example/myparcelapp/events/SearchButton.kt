package com.example.myparcelapp.events

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.SearchView
import androidx.annotation.RequiresApi
import com.example.myparcelapp.view.CategoryActivity
import com.example.myparcelapp.view.SearchActivity
import com.example.myparcelapp.view.SearchresultActivity

class SearchButton(var activity: Activity, var context: Context, var flt: String, var any: String) : SearchView.OnQueryTextListener {
    var st:String
    var tag:String
    var br:String
    var agn:String

    init {
        this.st="0"
        this.tag=""
        this.br=""
        this.agn="0"
    }

    fun setOptionsFlt(flt:String){
        this.flt=flt
    }
    fun setOptionsStar(st:String){
        this.st=st
    }
    fun setOptionsTag(tag:String){
        this.tag=tag
    }
    fun setOptionsBrand(br:String){
        this.br=br
    }
    fun setOptionsAlign(agn:String){
        this.agn=agn
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onQueryTextSubmit(query: String): Boolean {
       if(activity.javaClass == SearchresultActivity::class.java){
           var tagTemp =tag
           var brTemp =br
           if (tagTemp == any){
               tagTemp = ""
           }
           if (brTemp == any){
               brTemp = ""
           }
           (activity as SearchresultActivity).searchResultListInitialize(query,flt,st,tagTemp,brTemp,agn,activity)
       }
        else{
           (activity as SearchActivity).startActivitySearchResultActivity(query, flt)
       }
       return true

    }
    //텍스트가 바뀔때마다 호출
    override fun onQueryTextChange(newText: String): Boolean {
        return true
    }

}