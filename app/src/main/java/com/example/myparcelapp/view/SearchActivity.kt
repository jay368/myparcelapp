package com.example.myparcelapp.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import com.example.myparcelapp.R
import com.example.myparcelapp.events.SearchButton
import com.example.myparcelapp.utils.ActivityTransferManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_searchresult.*

class SearchActivity : Activity() , BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var search_button_event: SearchButton
    var IP=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        IP = resources.getString(R.string.homepageIP)

        val wb: WebView = WebView(this)
        wb.loadUrl(IP+"/sessiontest/")
        //로그인 해결되기 전까진 이렇게 한다.
        search_button_event = SearchButton(
            this,
            applicationContext,
            spinner_searchfilter.selectedItemId.toString(),
            resources.getStringArray(R.array.productkind)[0].toString()
        )
        searchview1.isSubmitButtonEnabled = true;
        searchview1.setOnQueryTextListener(search_button_event)
        searchview1.isSubmitButtonEnabled = true;
        searchview1.setOnQueryTextListener(search_button_event)

        spinner_searchfilter.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                search_button_event.SetOptions_flt(position.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.menu.findItem(R.id.search).isChecked=true
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }


    fun StartActivitySearchresultActivity(query:String, flt:String){
        val intent = Intent(applicationContext, SearchresultActivity::class.java)
        intent.putExtra("sch",query);//검색단어
        intent.putExtra("flt",flt);//필터
        intent.putExtra("st","0");//몇 별점 이상이여야만 검색되는가
        intent.putExtra("tag","");//태그
        intent.putExtra("br","");//브랜드
        intent.putExtra("agn","0");//정렬
        val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.selectedItemId= R.id.home
        startActivity(intent)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        return ActivityTransferManager.startActivityByBottomTabClick(this,
            R.id.search, p0.itemId)
    }
}
