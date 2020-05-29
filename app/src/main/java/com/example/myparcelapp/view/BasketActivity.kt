package com.example.myparcelapp.view

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myparcelapp.R
import com.example.myparcelapp.utils.RetrofitClientInstance
import com.example.myparcelapp.model.BasketProductVOList
import com.example.myparcelapp.events.ProductPageOpenOnClick
import com.example.myparcelapp.service.BasketProductService
import com.example.myparcelapp.utils.ActivityTransferManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_basket.*
import kotlinx.android.synthetic.main.activity_basket.navigationView
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.basket_product.view.*
import kotlinx.android.synthetic.main.basket_product.view.bp_editText_num
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.reflect.jvm.internal.impl.util.Check


class BasketActivity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener {
    var bp_check: ArrayList<CheckBox> = ArrayList<CheckBox>()
    lateinit var wb: WebView
    var IP=""
    var basket_product_opened:Boolean=false
    var BuyingProduct_total:Int = 0
    var buyIndexlist = ArrayList<String>()
    var buyNumlist = ArrayList<Int>()
    var buyTitlelist = ArrayList<String>()
    var buyImagelist = ArrayList<String>()
    var allchecking = false //모두체크 진행중이면 true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)
        IP = resources.getString(R.string.homepageIP)

        var ab = supportActionBar!!
        ab.setDisplayShowCustomEnabled(true) //커스터마이징 하기 위해 필요
        ab.setDisplayShowTitleEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)

        Log.d("BasketListInitialize()","BasketListInitialize()")
        //밑의 바로구매 버튼이 있는 아래 네비게이션 매뉴

        wb = WebView(this)
        wb.loadUrl(IP+"/sessiontest/")
        wb.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                bp_check!!.forEach{
                    it.isEnabled=true
                }//다시 한꺼번에 불러올 필요 없이 체크할 때 총액만 수정되는 방식을 택함
                basketListInitialize_one()
            }
        }
        //로그인 문제가 해결되기 전까지는 임시적으로 웹뷰를 통해 로그인용 쿠키를 얻는다.
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.menu.findItem(R.id.basket).isChecked=true
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bp_check!!.forEach{
            it.isEnabled=false
        }
        basket_product_opened=false
        wb.loadUrl(IP+"/sessiontest/")
    }

    fun onclick7(v:View) {//한번에 모두체크 버튼
        Log.d("url :: ", IP+"/basketupdateallcheck?chk=1")
        wb.loadUrl(IP+"/basketupdateallcheck?chk=1")
        allchecking = true
        bp_check!!.forEach{
            if(!it.isChecked)it.isChecked=true
        }
        allchecking = false
    }

    fun basketListInitialize_one(){
        basketListInitialize(this)
    }

    fun basketListInitialize(activity: Activity){

        val service = RetrofitClientInstance.retrofitInstance?.create(BasketProductService::class.java)
        val call = service?.basketProductList(resources.getString(R.string.temporarilyUsercode))
        //세션 문제가 해결되기 전까지는 임시로 OA==로 한다.
        Log.d("service :: ", service?.toString())
        Log.d("call :: ", call?.toString())

        call?.enqueue(object : Callback<BasketProductVOList>{
            override fun onFailure(call: Call<BasketProductVOList>, t: Throwable) {
                Log.d("Error :: ", t.toString())
            }

            override fun onResponse(
                call: Call<BasketProductVOList>?,
                response: Response<BasketProductVOList>?
            ) {
                Log.d("Response :: ", response?.toString())
                val body = response?.body()
                val list = body?.bpl
                var size = list?.size
                Log.i("list :: ", list.toString())

                val paytext = getString(R.string.pay)

                when (basket_product_opened) {
                    false -> {
                        buyIndexlist.clear()
                        buyNumlist.clear()
                        buyTitlelist.clear()
                        buyImagelist.clear()
                        bp_check.clear()
                        basket_products.removeAllViews()
                        list!!.forEach {
                            val bp = LayoutInflater.from(applicationContext)
                                .inflate(R.layout.basket_product, basket_products, false)
                            basket_products.addView(bp)
                            val oc = ProductPageOpenOnClick(
                                activity,
                                applicationContext,
                                it.product_index
                            )
                            bp.imageView_bp.setOnClickListener(oc)
                            bp.bp_textView_pay.setText(paytext + " : " + it.pay.toString() + " KRW")
                            bp.bp_textView_pname.setText(it.name)
                            bp.bp_editText_num.setText(it.num.toString())
                            bp.bp_editText_num.addTextChangedListener(object : TextWatcher {
                                //갯수 에딧텍스트가 변경되면 DB 업데이트를 시전한다.
                                override fun afterTextChanged(s: Editable?) {
                                    Log.d(
                                        "url :: ",
                                        IP + "/basketupdate?num=" + s + "&p=" + it.product_index + "&flag=1"
                                    )
                                    if (s != null) {
                                        wb.loadUrl(IP + "/basketupdate?num=" + s + "&p=" + it.product_index + "&flag=1")
                                        var updateindex = buyIndexlist.indexOf(it.product_index)
                                        if(s.isNotEmpty())
                                            buyNumlist[updateindex] = s.toString().toInt()
                                    }
                                }

                                override fun beforeTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    count: Int,
                                    after: Int
                                ) {
                                }

                                override fun onTextChanged(
                                    s: CharSequence?,
                                    start: Int,
                                    before: Int,
                                    count: Int
                                ) {
                                }

                            })
                            bp.bp_checkBox.isChecked = (it.checked == 1)
                            bp.bp_checkBox.setOnCheckedChangeListener { arg0, arg1 ->
                                //체크하면 DB에서 체크 데이터가 갱신된다.\
                                when(bp.bp_checkBox.isChecked) {
                                    true -> {
                                        if(!allchecking) wb.loadUrl(IP + "/basketupdatecheck?chk=1&p=" + it.product_index)//모두체크 진행중이 아닐때만 DB에 개별 체크 상태가 저장됨
                                        buyIndexlist.add(it.product_index)
                                        buyNumlist.add(it.num)
                                        buyTitlelist.add(it.name)
                                        buyImagelist.add(IP + it.img)
                                    }
                                    false ->{
                                        if(!allchecking) wb.loadUrl(IP + "/basketupdatecheck?chk=0&p=" + it.product_index)
                                        var removeindex = buyIndexlist.indexOf(it.product_index)
                                        buyIndexlist.removeAt(removeindex)
                                        buyNumlist.removeAt(removeindex)
                                        buyTitlelist.removeAt(removeindex)
                                        buyImagelist.removeAt(removeindex)
                                    }
                                }
                            }
                            bp_check.add(bp.bp_checkBox)
                            var tempit = it//중첩 it는 안 되길래 포인터를 임시저장
                            bp.deletebutton.setOnClickListener {
                                Log.d(
                                    "url :: ",
                                    IP + "/basketdelete?b_productindex=" + tempit.product_index
                                )
                                wb.loadUrl(IP + "/basketdelete?b_productindex=" + tempit.product_index)
                                basket_products.removeView(bp)
                                var removeindex = buyIndexlist.indexOf(tempit.product_index)
                                buyIndexlist.removeAt(removeindex)
                                buyNumlist.removeAt(removeindex)
                                buyTitlelist.removeAt(removeindex)
                                buyImagelist.removeAt(removeindex)
                            }
                            if(bp.bp_checkBox.isChecked) {
                                buyIndexlist.add(it.product_index)
                                buyNumlist.add(it.num)
                                buyTitlelist.add(it.name)
                                buyImagelist.add(IP + it.img)
                            }

                            val imgurl = Uri.parse("http://192.168.55.231:8080" + it.img)
                            Glide.with(applicationContext).load(imgurl).into(bp.imageView_bp)
                        }
                        Log.i("bp_check :: ", bp_check.toString())
                        basket_product_opened = true
                    }
                }

                val totaltextstart=getString(R.string.bottom_menu_totalpay)
                val totaltext = " : "+body?.total
                BuyingProduct_total = body?.total.toString().toInt()
                textView_totalpay.text = totaltextstart+totaltext

                Log.d("body :: ", body.toString())
                Log.d("list :: ", list.toString())
                Log.d("size :: ", size.toString())
                //데이터가 제대로 잘 들어갔는지 확인

            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_basket, menu)
        return true
    }



    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        return ActivityTransferManager.startActivityByBottomTabClick(this,
            R.id.basket, p0.itemId)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onclick_Buy(v: View) {
        ActivityTransferManager.startActivityBuyButtonClick(
            this,
            buyIndexlist,
            buyNumlist,
            buyTitlelist,
            buyImagelist,
            BuyingProduct_total
        )
    }

}



