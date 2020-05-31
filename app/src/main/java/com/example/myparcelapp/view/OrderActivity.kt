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
import com.example.myparcelapp.MyApplication
import com.example.myparcelapp.R
import com.example.myparcelapp.utils.RetrofitClientInstance
import com.example.myparcelapp.model.OrderProductsVO
import com.example.myparcelapp.model.OrderVO
import com.example.myparcelapp.model.OrderVOList
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
import java.util.*

class OrderActivity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener{


    val IP = getString(R.string.homepageIP)

    lateinit var wb: WebView
    var order_opened: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_)

        val ab = supportActionBar?.apply {
            this.setDisplayShowCustomEnabled(true) //커스터마이징 하기 위해 필요
            this.setDisplayShowTitleEnabled(true)
            this.setDisplayHomeAsUpEnabled(true)
        }

        wb = WebView(this)
        wb.loadUrl("$IP/sessiontest/")
        wb.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                if(!order_opened) orderInitialize()//다시 한꺼번에 불러올 필요 없이 삭제할 때 DB만 수정되게 하는 방식을 택함
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.menu.findItem(R.id.order).isChecked = true
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        order_opened=false
        wb.loadUrl("$IP/sessiontest/")
    }

    fun orderInitialize(){

        val service = RetrofitClientInstance.retrofitInstance?.create(OrderService::class.java)
        val call = service?.orderList(resources.getString(R.string.temporarilyUsercode))

        //세션 문제가 해결되기 전까지는 임시로 OA==로 한다.
        Log.d(MyApplication.LogTag, "service :: ${service?.toString()}")
        Log.d(MyApplication.LogTag,"call :: ${call?.toString()}")

        call?.enqueue(object : Callback<OrderVOList> {
            override fun onFailure(call: Call<OrderVOList>, t: Throwable) {
                Log.d("Error :: ", t.toString())
            }

            override fun onResponse(call: Call<OrderVOList>?, response: Response<OrderVOList>?) {

                Log.d(MyApplication.LogTag, "Response :: ${response?.toString()}")

                response
                    ?.takeIf { it.isSuccessful }
                    ?.let {

                        it.body()?.ol?.forEach { od ->
                            setOrderListView(od)
                        }

                    }

                val body = response?.body()
                val list = body?.ol
                var size = list?.size
                Log.i("list :: ", list.toString())

                for (i in list!!){

                }


                Log.d("body :: ", body.toString())
                Log.d("list :: ", list.toString())
                Log.d("size :: ", size.toString())


            }

        })
        order_opened = true
    }

    private fun setOrderListView(order: OrderVO) {

        val inflater = LayoutInflater.from(applicationContext)
        val ol = inflater.inflate(R.layout.layout_order, orders, false)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

        orders.removeAllViews()

        val olTxt = "${getString(R.string.bottom_menu_totalpay)} : ${order.total}KRW\n" +
                "${getString(R.string.shipping_place)} : ${order.shipping_place}\n" +
                "${getString(R.string.ordered_day)} : ${dateFormat.format(order.day)}"
        ol.ol_textView.text = olTxt

        ol.orderDeletebutton.setOnClickListener {
            orders.removeView(ol)
            Log.d(MyApplication.LogTag, "url :: $IP/orderdelete?o_index=${order.index}")
            wb.loadUrl("$IP/orderdelete?o_index=${order.index}")
        }

        orders.addView(ol)
        Log.i(MyApplication.LogTag, "i.prds :: ${order.prds}")
        olpAddView(order.prds, ol, inflater)

    }

    private fun olpAddView(list :List<OrderProductsVO>, ol: View, inflater:LayoutInflater){

        list.forEach {

            val olp = inflater.inflate(R.layout.layout_order_product, layout_olp_list, false)
            ol.layout_olp_list.addView(olp)
            olp.olp_textView.text = "${it.name}(${it.num})"

            Glide.with(this)
                .load(Uri.parse("$IP${it.img}"))
                .into(olp.olp_imageView)
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
        return when(item?.itemId){
            R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
    }

}
