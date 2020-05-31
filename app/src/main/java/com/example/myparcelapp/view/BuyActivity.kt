package com.example.myparcelapp.view

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myparcelapp.R
import com.example.myparcelapp.model.UserVO
import com.example.myparcelapp.service.UserService
import com.example.myparcelapp.utils.RetrofitClientInstance
import kotlinx.android.synthetic.main.activity_buy.*
import kotlinx.android.synthetic.main.layout_order_product.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLEncoder


class BuyActivity : Activity() {
    lateinit var wb:WebView
    private lateinit var buyIndexList:ArrayList<String>
    private lateinit var buyNumList:ArrayList<Int>
    private lateinit var buyTitleList:ArrayList<String>
    private lateinit var buyImageList:ArrayList<String>
    var total:Int = 0
    private var ip=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)
        val intent = intent
        ip = resources.getString(R.string.homepageIP)
        buyIndexList = intent.getStringArrayListExtra("buyIndexList")
        buyNumList = intent.getIntegerArrayListExtra("buyNumList")
        buyTitleList = intent.getStringArrayListExtra("buyTitleList")
        buyImageList = intent.getStringArrayListExtra("buyImageList")
        total = intent.getIntExtra("total",0)
        wb = WebView(this)
        wb.loadUrl("$ip/sessiontest/")
        wb.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url:String) {
                super.onPageFinished(view, url)
                buyInitialize()
            }
        }
    }

    fun buyInitialize(){
        var i = 0
        val intent = getIntent()
        textview_total.text = intent.getStringExtra("total")
        for (buyIndex in buyIndexList){//같은 브랜드 상품

            val td = LayoutInflater.from(applicationContext).inflate(R.layout.layout_order_product, orderlist, false)
            orderlist.addView(td)
            td.olp_textView.text = "${buyTitleList[i]}\n${resources.getString(R.string.buy_num)} : ${buyNumList[i]}"
            val imgUrl = Uri.parse(buyImageList[i])
            Glide.with(applicationContext).load(imgUrl).into(td.olp_imageView)
            i += 1
        }

        textview_total.text = total.toString()
        val service = RetrofitClientInstance.retrofitInstance?.create(UserService::class.java)
        val call = service?.user(resources.getString(R.string.temporarilyUsercode))
        call?.enqueue(object : Callback<UserVO> {
            override fun onFailure(call: Call<UserVO>, t: Throwable) {
                Log.d("Error :: ", t.toString())
            }

            override fun onResponse(call: Call<UserVO>?, response: Response<UserVO>?
            ) {
                val body = response?.body()
                textview_orderer.text = body?.name
            }
        })
    }

    @SuppressLint("ShowToast")
    fun onClickOrdering(v:View){
        if(text_address.text.isEmpty()) {//주소에 아무것도 없을 경우
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.PleaseAddressInput),
                Toast.LENGTH_SHORT
            )//배송지를 입력해 주세요
            return
        }


        var p = ""//서버에서 상품1_상품2_상품3으로 나눈다.
        var num = ""//서버에서 갯수1_갯수2_갯수3으로 나눈다.
        var i = 0
        buyIndexList.forEach {
            p += it
            i += 1
            if(i < buyIndexList.size)
                p += "_"
        }
        i = 0
        buyNumList.forEach {
            num += it
            i += 1
            if(i < buyNumList.size)
                num += "_"
        }

        val parameter = "p=${URLEncoder.encode(p, "UTF-8")}&num=${URLEncoder.encode(num, "UTF-8")}&ordererIndex=${resources.getString(R.string.temporarilyUsercode)}&area=${text_address.text}&flag=1"
        wb.postUrl("$ip/ordering", parameter.toByteArray())
        Toast.makeText(this,resources.getString(R.string.OrderingComplete),Toast.LENGTH_LONG).show()
        finish()
    }//주문버튼
}
