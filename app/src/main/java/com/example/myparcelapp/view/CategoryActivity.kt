package com.example.myparcelapp.view

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.annotation.RequiresApi
import com.example.myparcelapp.R
import com.example.myparcelapp.utils.ActivityTransferManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_category.navigationView

class CategoryActivity : Activity() , BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        LayoutsLoad()
    }

    override fun onResume() {
        super.onResume()
        LayoutsLoad()
        val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.menu.findItem(R.id.category).isChecked=true
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    fun LayoutsLoad(){//카테고리 레이아웃
        var categorystr:Array<String> = resources.getStringArray(R.array.productkind);

        var strindex=0;
        var i = 0
        while (true){
            var row = TableRow(this);
            row.layoutParams= TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
            row.gravity = Gravity.FILL
            row.textAlignment = View.TEXT_ALIGNMENT_CENTER
            for (j in 0 ..3){
                var newbt = Button(this)
                newbt.setText(categorystr.get(strindex))
                newbt.setTextSize(15F)
                newbt.layoutParams= TableRow.LayoutParams(0, 180, 1f)
                row.addView(newbt)

                strindex+=1
                if(strindex>=categorystr.size)break
            }
            table_category.addView(row)
            if(strindex>=categorystr.size)break
            i++
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        return return ActivityTransferManager.startActivityByBottomTabClick(this,
            R.id.category, p0.itemId)
    }
}