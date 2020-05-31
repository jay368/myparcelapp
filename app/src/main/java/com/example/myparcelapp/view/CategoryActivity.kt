package com.example.myparcelapp.view

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.annotation.RequiresApi
import com.example.myparcelapp.R
import com.example.myparcelapp.events.SearchButton
import com.example.myparcelapp.utils.ActivityTransferManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_category.navigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_searchresult.*

class CategoryActivity : Activity() , BottomNavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
    }

    override fun onResume() {
        super.onResume()
        layoutsLoad()
        val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.menu.findItem(R.id.category).isChecked=true
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    private fun layoutsLoad(){//카테고리 레이아웃
        val categoryStr:Array<String> = resources.getStringArray(R.array.productKind)
        table_category.removeAllViews()//뷰 초기화
        var strIndex = 0
        var i = 0
        runnable@ while (true) {
            val row = TableRow(this)
            row.layoutParams = TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
            row.gravity = Gravity.FILL
            row.textAlignment = View.TEXT_ALIGNMENT_CENTER
            listOf(0, 1, 2, 3).forEachIndexed { index, value ->
                val newButton = Button(this)
                newButton.text = categoryStr[strIndex]
                newButton.textSize = 15F
                newButton.layoutParams = TableRow.LayoutParams(0, 180, 1f)
                newButton.setOnClickListener {
                    val intent = Intent(applicationContext, SearchresultActivity::class.java)
                    val fltIndex = categoryStr.indexOf(newButton.text.toString())
                    intent.putExtra("sch","")//검색단어
                    intent.putExtra("flt","$fltIndex")//필터
                    intent.putExtra("st","0")//몇 별점 이상이여야만 검색되는가
                    intent.putExtra("tag","")//태그
                    intent.putExtra("br","")//브랜드
                    intent.putExtra("agn","0")//정렬
                    startActivity(intent)
                }
                row.addView(newButton)

                strIndex += 1
                if (strIndex >= categoryStr.size) return@forEachIndexed
            }
            table_category.addView(row)
            if (strIndex >= categoryStr.size) break@runnable
            i++
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        return ActivityTransferManager.startActivityByBottomTabClick(this, R.id.category, p0.itemId)
    }
}
