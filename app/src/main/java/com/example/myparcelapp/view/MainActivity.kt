package com.example.myparcelapp.view

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.myparcelapp.*
import com.example.myparcelapp.model.ProductVOList
import com.example.myparcelapp.events.ProductPageOpenOnClick
import com.example.myparcelapp.events.SearchButton
import com.example.myparcelapp.model.ProductVO
import com.example.myparcelapp.service.TodayDealService
import com.example.myparcelapp.utils.ActivityTransferManager
import com.example.myparcelapp.utils.RetrofitClientInstance
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.standard_product.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : Activity() , BottomNavigationView.OnNavigationItemSelectedListener{


    lateinit var search_button_event: SearchButton
    val IP = getString(R.string.homepageIP)


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val wb: WebView = WebView(this)
        wb.loadUrl("$IP/sessiontest/")
        //로그인 해결되기 전까진 이렇게 한다.
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.menu.findItem(R.id.home).isChecked = true
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        todayDealInitialize(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        return ActivityTransferManager.startActivityByBottomTabClick(this,
            R.id.home, p0.itemId)
    }



    private fun todayDealInitialize(activity: Activity){

        val service = RetrofitClientInstance.retrofitInstance?.create(TodayDealService::class.java)
        val call = service?.todaydeallist()
        Log.d(MyApplication.LogTag, "service :: ${service?.toString()}")
        Log.d(MyApplication.LogTag, "call :: ${call?.toString()}")

        call?.enqueue(object : Callback<ProductVOList> {
            override fun onFailure(call: Call<ProductVOList>, t: Throwable) {
                Log.d("Error :: ", t.toString())
            }

            override fun onResponse(call: Call<ProductVOList>?, response: Response<ProductVOList>?) {

                Log.d(MyApplication.LogTag, "Response :: ${response?.toString()}")

                response
                    ?.takeIf { it.isSuccessful }
                    ?.let {

                        it.body()?.prdlist?.forEach { pd ->
                            setTodayDealListView(pd)
                        }

                    }
            }

        })
    }


    private fun setTodayDealListView(productInfo: ProductVO) {

        todaydeallist.removeAllViews()

        val td = LayoutInflater.from(this).inflate(R.layout.standard_product, todaydeallist, false)
        val oc = ProductPageOpenOnClick(this, productInfo.index)

        todaydeallist.addView(td)
        td.td_name.text = productInfo.name
        td.td_pay.text = "${getString(R.string.pay)} : ${productInfo.pay}KRW"
        td.td_star.progress = productInfo.star
        td.layout_bp.setOnClickListener(oc)

        val imgUrl = Uri.parse("$IP${productInfo.img}")
        Glide.with(this).load(imgUrl).into(td.imageView_td)
        Log.d(MyApplication.LogTag, "productInfo :: $productInfo")

    }



}


