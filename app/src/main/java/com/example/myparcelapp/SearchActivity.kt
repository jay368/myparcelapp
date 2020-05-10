package com.example.myparcelapp

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.navigationView
import kotlinx.android.synthetic.main.activity_searchresult.*

class SearchActivity : Activity() , BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var search_button_event:SearchButton
    var IP=""
    lateinit var bottomNavigationView : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        IP = resources.getString(R.string.homepageIP)

        bottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.search

        val wb: WebView = WebView(this)
        wb.loadUrl(IP+"/sessiontest/")
        //로그인 해결되기 전까진 이렇게 한다.
        search_button_event = SearchButton(this,
            applicationContext,
            spinner_searchfilter.selectedItemId.toString(),
            resources.getStringArray(R.array.productkind)[0].toString())
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

    fun StartActivitySearchresultActivity(query:String, flt:String){
        val intent = Intent(applicationContext, SearchresultActivity::class.java)
        intent.putExtra("sch",query);//검색단어
        intent.putExtra("flt",flt);//필터
        intent.putExtra("st","0");//몇 별점 이상이여야만 검색되는가
        intent.putExtra("tag","");//태그
        intent.putExtra("br","");//브랜드
        intent.putExtra("agn","0");//정렬
        startActivity(intent)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.category ->{
                intent = Intent(this, CategoryActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                bottomNavigationView.selectedItemId = R.id.search
                startActivity(intent , ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                return true
            }
            R.id.search -> {
                return true
            }
            R.id.home -> {
                intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                bottomNavigationView.selectedItemId = R.id.search
                startActivity(intent , ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                return true
            }
            R.id.basket -> {
                intent = Intent(this, BasketActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                bottomNavigationView.selectedItemId = R.id.search
                startActivity(intent , ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                return true
            }
            R.id.order -> {
                intent = Intent(this, Order_Activity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                bottomNavigationView.selectedItemId = R.id.search
                startActivity(intent , ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                return true
            }
        }
        return true
    }
}
