package com.example.myparcelapp.view

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.myparcelapp.R
import com.example.myparcelapp.utils.RetrofitClientInstance
import com.example.myparcelapp.dto.OrderProductsVO
import com.example.myparcelapp.dto.OrderVOList
import com.example.myparcelapp.service.OrderService
import com.example.myparcelapp.utils.ActivityTransferManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_order_.*
import kotlinx.android.synthetic.main.activity_order_.navigationView
import kotlinx.android.synthetic.main.layout_order.*
import kotlinx.android.synthetic.main.layout_order.view.*
import kotlinx.android.synthetic.main.layout_order_product.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class Order_Activity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener{

    var IP=""
    lateinit var wb: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_)
        IP = resources.getString(R.string.homepageIP)

        var ab = supportActionBar!!
        ab.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        wb = WebView(this)
        wb.loadUrl(IP+"/sessiontest/")
        wb.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                OrderInitialize()
            }
        };
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.menu.findItem(R.id.order).isChecked=true
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    fun OrderInitialize(){

        val service = RetrofitClientInstance.retrofitInstance?.create(OrderService::class.java);
        val call = service?.orderList(resources.getString(R.string.temporarilyUsercode))
        //세션 문제가 해결되기 전까지는 임시로 OA==로 한다.
        Log.d("service :: ", service?.toString())
        Log.d("call :: ", call?.toString())

        call?.enqueue(object : Callback<OrderVOList> {
            override fun onFailure(call: Call<OrderVOList>, t: Throwable) {
                Log.d("Error :: ", t.toString())
            }

            override fun onResponse(
                call: Call<OrderVOList>?,
                response: Response<OrderVOList>?
            ) {
                Log.d("Response :: ", response?.toString())
                val body = response?.body()
                val list = body?.ol
                var size = list?.size
                Log.i("list :: ", list.toString())

                val inflater = LayoutInflater.from(applicationContext)
                val dateformat = SimpleDateFormat("yyyy-MM-dd");
                orders.removeAllViews()

                for (i in list!!){
                    val ol = inflater.inflate(R.layout.layout_order, orders, false);
                    ol.ol_textView.setText(
                        getText(R.string.bottom_menu_totalpay).toString()+" : "+i.total+" KRW"+"\n"+
                                getText(R.string.shipping_place).toString()+" : "+i.shipping_place+"\n"+
                                getText(R.string.ordered_day).toString()+" : "+dateformat.format(i.day))
                    ol.orderDeletebutton.setOnClickListener(object : View.OnClickListener {//주문내역삭제
                    override fun onClick(v: View?) {
                        Log.d("url :: ", IP+"/orderdelete?o_index="+i.index)
                        wb.loadUrl(IP+"/orderdelete?o_index="+i.index)
                    }
                    })
                    orders.addView(ol)
                    Log.i("i.prds :: ", i.prds.toString())
                    OlpAddview(i.prds,ol,inflater)
                }


                Log.d("body :: ", body.toString())
                Log.d("list :: ", list.toString())
                Log.d("size :: ", size.toString())


            }

        })
    }

    fun OlpAddview(list :List<OrderProductsVO>, ol: View, inflater:LayoutInflater){
        for (j in list!!) {
            val olp = inflater.inflate(R.layout.layout_order_product, layout_olp_list, false);
            ol.layout_olp_list.addView(olp)
            olp.olp_textView.setText(j.name+"("+j.num+")")
            val imgurl = Uri.parse(IP+j.img)
            Glide.with(applicationContext).load(imgurl).into(olp.olp_imageView);
        }
    }




    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        return ActivityTransferManager.startActivityByBottomTabClick(this,
            R.id.order, p0.itemId)
    }





    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_order, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.home -> {
                finish()
                return true;
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
    }

}
