package com.example.myparcelapp

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.myparcelapp.dto.ProductVOList
import com.example.myparcelapp.service.TodayDealService
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_searchresult.*
import kotlinx.android.synthetic.main.standard_product.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : Activity() , BottomNavigationView.OnNavigationItemSelectedListener{

    lateinit var search_button_event:SearchButton
    var IP=""
    lateinit var bottomNavigationView : BottomNavigationView

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        IP = resources.getString(R.string.homepageIP)

        bottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bottomNavigationView.selectedItemId = R.id.home

        val wb: WebView = WebView(this)
        wb.loadUrl(IP+"/sessiontest/")
        //로그인 해결되기 전까진 이렇게 한다.

        TodayDealInitialize(this)
    }



    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        var intent:Intent
        when(p0.itemId){
            R.id.category ->{
                intent = Intent(this, CategoryActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent , ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                bottomNavigationView.selectedItemId = R.id.home
                return true
            }
            R.id.search -> {
                intent = Intent(this, SearchActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent , ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                bottomNavigationView.selectedItemId = R.id.home
                return true
            }
            R.id.home -> {
                return true
            }
            R.id.basket -> {
                intent = Intent(this, BasketActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent , ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                bottomNavigationView.selectedItemId = R.id.home
                return true
            }
            R.id.order -> {
                intent = Intent(this, Order_Activity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent , ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                bottomNavigationView.selectedItemId = R.id.home
                return true
            }
        }
        return true
    }








    fun TodayDealInitialize(activity:Activity){

        val service = RetrofitClientInstance.retrofitInstance?.create(TodayDealService::class.java);
        val call = service?.todaydeallist()
        Log.d("service :: ", service?.toString())
        Log.d("call :: ", call?.toString())

        call?.enqueue(object : Callback<ProductVOList> {
            override fun onFailure(call: Call<ProductVOList>, t: Throwable) {
                Log.d("Error :: ", t.toString())
            }

            override fun onResponse(
                call: Call<ProductVOList>?,
                response: Response<ProductVOList>?
            ) {
                todaydeallist.removeAllViews()
                Log.d("Response :: ", response?.toString())
                val body = response?.body()
                val list = body?.prdlist
                var size = list?.size
                Log.i("list :: ", list.toString())
                val paytext = getString(R.string.pay);

                //
                for (i in list!!){
                    val td = LayoutInflater.from(applicationContext).inflate(R.layout.standard_product, todaydeallist, false);
                    val oc:ProductPageOpenOnClick = ProductPageOpenOnClick(activity, applicationContext, i.index)
                    todaydeallist.addView(td)
                    td.td_name.setText(i.name.toString())
                    td.td_pay.setText(paytext+" : "+i.pay.toString()+"KRW")
                    td.td_star.progress=i.star;
                    td.layout_bp.setOnClickListener(oc)

                    val imgurl = Uri.parse(IP+i.img)
                    Glide.with(applicationContext).load(imgurl).into(td.imageView_td);
                    Log.d("i :: ", i.toString())
                }


                Log.d("body :: ", body.toString())
                Log.d("list :: ", list.toString())
                Log.d("size :: ", size.toString())


            }

        })
    }


    fun ProductPageOpen(index:String){
        val intent = Intent(applicationContext, ProductActivity::class.java)
        intent.putExtra("pid", index)
        startActivity(intent)
    }


}


