package com.example.myparcelapp.view

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import java.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.myparcelapp.R
import com.example.myparcelapp.dto.UserVO
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
    lateinit var buyIndexlist:ArrayList<String>
    lateinit var buyNumlist:ArrayList<Int>
    lateinit var buyTitlelist:ArrayList<String>
    lateinit var buyImagelist:ArrayList<String>
    var total:Int = 0
    var IP=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy)
        val intent = getIntent()
        IP = resources.getString(R.string.homepageIP)
        buyIndexlist = intent.getStringArrayListExtra("buyIndexlist")
        buyNumlist = intent.getIntegerArrayListExtra("buyNumlist")
        buyTitlelist = intent.getStringArrayListExtra("buyTitlelist")
        buyImagelist = intent.getStringArrayListExtra("buyImagelist")
        total = intent.getIntExtra("total",0)
        wb = WebView(this)
        wb.loadUrl(IP+"/sessiontest/")
        wb.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url:String) {
                super.onPageFinished(view, url)
                BuyInitialize()
            }
        };
    }

    fun BuyInitialize(){
        var i = 0
        val intent = getIntent()
        textview_total.text=intent.getStringExtra("total")
        for (buyindex in buyIndexlist){//같은 브랜드 상품

            val td = LayoutInflater.from(applicationContext).inflate(R.layout.layout_order_product, orderlist, false);
            orderlist.addView(td)
            td.olp_textView.setText(buyTitlelist.get(i)+"\n"+resources.getString(R.string.buy_num)+" : "+buyNumlist.get(i))
            val imgurl = Uri.parse(buyImagelist[i])
            Glide.with(applicationContext).load(imgurl).into(td.olp_imageView);
            i+=1
        }

        textview_total.text=total.toString()
        val service = RetrofitClientInstance.retrofitInstance?.create(UserService::class.java);
        val call = service?.user(resources.getString(R.string.temporarilyUsercode))
        call?.enqueue(object : Callback<UserVO> {
            override fun onFailure(call: Call<UserVO>, t: Throwable) {
                Log.d("Error :: ", t.toString())
            }

            override fun onResponse(call: Call<UserVO>?, response: Response<UserVO>?
            ) {
                val body = response?.body()
                textview_orderer.text= body?.name
            }
        })
    }

    fun OnClickOrdering(v:View){
        if(text_address.text.isEmpty()) {//주소에 아무것도 없을 경우
            Toast.makeText(
                applicationContext,
                resources.getString(R.string.PleaseAddressInput),
                Toast.LENGTH_SHORT
            )//배송지를 입력해 주세요
            return
        }


        var p:String = ""//서버에서 상품1_상품2_상품3으로 나눈다.
        var num:String = ""//서버에서 갯수1_갯수2_갯수3으로 나눈다.
        var i:Int=0
        for (str in buyIndexlist){
            p+= str
            i+=1
            if(i < buyIndexlist.size)
                p+="_"
        }//배열을 _문자열로 변환
        i=0
        for (str in buyNumlist){
            num+=str
            i+=1
            if(i < buyNumlist.size)
                num+="_"
        }

        val parameter = "p=" + URLEncoder.encode(p, "UTF-8").toString() +
                        "&num=" + URLEncoder.encode(num, "UTF-8").toString() +
                        "&ordererIndex=" + resources.getString(R.string.temporarilyUsercode) +
                        "&area=" + text_address.text.toString() +
                        "&flag=1"
        wb.postUrl(IP+"/ordering", parameter.toByteArray())
        finish()
    }//주문버튼
}
