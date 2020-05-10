package com.example.myparcelapp

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.annotation.RequiresApi
import com.example.myparcelapp.utils.ActivityTransferManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_category.navigationView
import kotlinx.android.synthetic.main.activity_main.*

class CategoryActivity : Activity() , BottomNavigationView.OnNavigationItemSelectedListener {

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        LayoutsLoad()
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.selectedItemId=R.id.category
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
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
        return return ActivityTransferManager.startActivityByBottomTabClick(this, R.id.category, p0.itemId)
    }
}
