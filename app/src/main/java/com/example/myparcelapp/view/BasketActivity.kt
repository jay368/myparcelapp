package com.example.myparcelapp.view

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckBox
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myparcelapp.R
import com.example.myparcelapp.utils.RetrofitClientInstance
import com.example.myparcelapp.model.BasketProductVOList
import com.example.myparcelapp.events.ProductPageOpenOnClick
import com.example.myparcelapp.service.BasketProductService
import com.example.myparcelapp.utils.ActivityTransferManager
import com.example.myparcelapp.utils.Uuid
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_basket.*
import kotlinx.android.synthetic.main.activity_basket.navigationView
import kotlinx.android.synthetic.main.basket_product.view.*
import kotlinx.android.synthetic.main.basket_product.view.bp_editText_num
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BasketActivity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener {
    var bpCheck: ArrayList<CheckBox> = ArrayList<CheckBox>()
    lateinit var wb: WebView
    var ip=""
    var basketProductOpened:Boolean=false
    var buyingProductTotal:Int = 0
    var buyIndexList = ArrayList<String>()
    var buyNumList = ArrayList<Int>()
    var buyTitleList = ArrayList<String>()
    var buyImageList = ArrayList<String>()
    var allchecking = false //모두체크 진행중이면 true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)
        ip = resources.getString(R.string.homepageIP)

        val ab = supportActionBar!!
        ab.setDisplayShowCustomEnabled(true) //커스터마이징 하기 위해 필요
        ab.setDisplayShowTitleEnabled(true)
        ab.setDisplayHomeAsUpEnabled(true)

        Log.d("BasketListInitialize()","BasketListInitialize()")
        //밑의 바로구매 버튼이 있는 아래 네비게이션 매뉴

        wb = WebView(this)
        wb.loadUrl("$ip/sessiontest/?usercode=${Uuid.userIndex}")
        wb.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                bpCheck.forEach{
                    it.isEnabled = true
                }//다시 한꺼번에 불러올 필요 없이 체크할 때 총액만 수정되는 방식을 택함
                basketListInitializeOne()
            }
        }
        //로그인 문제가 해결되기 전까지는 임시적으로 웹뷰를 통해 로그인용 쿠키를 얻는다.
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.menu.findItem(R.id.basket).isChecked = true
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        bpCheck.forEach{
            it.isEnabled = false
        }
        basketProductOpened = false
        wb.loadUrl("$ip/sessiontest/?usercode=${Uuid.userIndex}")
    }

    fun onclickAllCheck(v:View) {//한번에 모두체크 버튼
        Log.d("url :: ", "$ip/basketupdateallcheck?chk=1")
        wb.loadUrl("$ip/basketupdateallcheck?chk=1")
        allchecking = true
        bpCheck.forEach{
            if(!it.isChecked)it.isChecked = true
        }
        allchecking = false
    }

    fun basketListInitializeOne(){
        basketListInitialize(this)
    }

    private fun basketListInitialize(activity: Activity){

        val service = RetrofitClientInstance.retrofitInstance?.create(BasketProductService::class.java)
        val call = service?.basketProductList(Uuid.userIndex)
        //세션 문제가 해결되기 전까지는 임시로 OA==로 한다.
        Log.d("service :: ", service.toString())
        Log.d("call :: ", call.toString())

        call?.enqueue(object : Callback<BasketProductVOList>{
            override fun onFailure(call: Call<BasketProductVOList>, t: Throwable) {
                Log.d("Error :: ", t.toString())
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<BasketProductVOList>?,
                response: Response<BasketProductVOList>?
            ) {
                Log.d("Response :: ", response.toString())
                val body = response?.body()
                val list = body?.bpl
                val size = list?.size
                Log.i("list :: ", list.toString())

                val paytext = getString(R.string.pay)

                when (basketProductOpened) {
                    false -> {
                        buyIndexList.clear()
                        buyNumList.clear()
                        buyTitleList.clear()
                        buyImageList.clear()
                        bpCheck.clear()
                        basket_products.removeAllViews()
                        list!!.forEach {
                            val bp = LayoutInflater.from(applicationContext)
                                .inflate(R.layout.basket_product, basket_products, false)
                            basket_products.addView(bp)
                            val oc = ProductPageOpenOnClick(
                                activity,
                                it.product_index
                            )
                            bp.imageView_bp.setOnClickListener(oc)
                            bp.bp_textView_pay.text = "$paytext : ${it.pay} KRW"
                            bp.bp_textView_pname.text = it.name
                            bp.bp_editText_num.setText(it.num.toString())
                            bp.bp_editText_num.addTextChangedListener(object : TextWatcher {
                                //갯수 에딧텍스트가 변경되면 DB 업데이트를 시전한다.
                                override fun afterTextChanged(s: Editable?) {
                                    Log.d(
                                        "url :: ",
                                        "$ip/basketupdate?num=$s&p=${it.product_index}&flag=1"
                                    )
                                    if (s != null) {
                                        wb.loadUrl("$ip/basketupdate?num=$s&p=${it.product_index}&flag=1")
                                        val updateindex = buyIndexList.indexOf(it.product_index)
                                        if(s.isNotEmpty())
                                            buyNumList[updateindex] = s.toString().toInt()
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
                                        if(!allchecking) wb.loadUrl("$ip/basketupdatecheck?chk=1&p=${it.product_index}")//모두체크 진행중이 아닐때만 DB에 개별 체크 상태가 저장됨
                                        buyIndexList.add(it.product_index)
                                        buyNumList.add(it.num)
                                        buyTitleList.add(it.name)
                                        buyImageList.add("$ip${it.img}")
                                    }
                                    false ->{
                                        if(!allchecking) wb.loadUrl("$ip/basketupdatecheck?chk=0&p=${it.product_index}")
                                        val removeIndex = buyIndexList.indexOf(it.product_index)
                                        buyIndexList.removeAt(removeIndex)
                                        buyNumList.removeAt(removeIndex)
                                        buyTitleList.removeAt(removeIndex)
                                        buyImageList.removeAt(removeIndex)
                                    }
                                }
                            }
                            bpCheck.add(bp.bp_checkBox)
                            val tempit = it//중첩 it는 안 되길래 포인터를 임시저장
                            bp.deletebutton.setOnClickListener {
                                Log.d(
                                    "url :: ",
                                    "$ip/basketdelete?b_productindex=${tempit.product_index}"
                                )
                                wb.loadUrl("$ip/basketdelete?b_productindex=${tempit.product_index}")
                                basket_products.removeView(bp)
                                val reindex = buyIndexList.indexOf(tempit.product_index)
                                buyIndexList.removeAt(reindex)
                                buyNumList.removeAt(reindex)
                                buyTitleList.removeAt(reindex)
                                buyImageList.removeAt(reindex)
                            }
                            if(bp.bp_checkBox.isChecked) {
                                buyIndexList.add(it.product_index)
                                buyNumList.add(it.num)
                                buyTitleList.add(it.name)
                                buyImageList.add("$ip${it.img}")
                            }

                            val imgurl = Uri.parse("http://192.168.55.231:8080${it.img}")
                            Glide.with(applicationContext).load(imgurl).into(bp.imageView_bp)
                        }
                        Log.i("bp_check :: ", bpCheck.toString())
                        basketProductOpened = true
                    }
                }

                val totaltextstart = getString(R.string.bottomMenuTotalpay)
                val totaltext = " : ${body?.total}"
                buyingProductTotal = body?.total.toString().toInt()
                textView_totalpay.text = "$totaltextstart$totaltext"

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



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
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
    fun onclickBuy(v: View) {
        ActivityTransferManager.startActivityBuyButtonClick(
            this,
            buyIndexList,
            buyNumList,
            buyTitleList,
            buyImageList,
            buyingProductTotal
        )
    }

}



