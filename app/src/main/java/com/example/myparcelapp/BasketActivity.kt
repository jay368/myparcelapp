package com.example.myparcelapp

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myparcelapp.dto.BasketProductVOList
import com.example.myparcelapp.service.BasketProductService
import com.example.myparcelapp.utils.ActivityTransferManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_basket.*
import kotlinx.android.synthetic.main.activity_basket.navigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.basket_product.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BasketActivity : AppCompatActivity() , BottomNavigationView.OnNavigationItemSelectedListener {
    var bp_check: ArrayList<CheckBox> = ArrayList<CheckBox>()
    lateinit var wb: WebView
    var IP=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)
        IP = resources.getString(R.string.homepageIP)

        var ab = supportActionBar!!
        ab.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        ab.setDisplayShowTitleEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        Log.d("BasketListInitialize()","BasketListInitialize()")
        //밑의 바로구매 버튼이 있는 아래 네비게이션 매뉴

        BasketListInitialize()
        wb = WebView(this)
        wb.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                BasketListInitialize()
            }
        });
        //로그인 문제가 해결되기 전까지는 임시적으로 웹뷰를 통해 로그인용 쿠키를 얻는다.
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.selectedItemId=R.id.basket
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
    }

    fun onclick7(v:View) {//한번에 모두체크 버튼
        for (i in bp_check!!) {
            Log.d("url :: ", IP+"/basketupdateallcheck?chk=1")
            wb.loadUrl(IP+"/basketupdateallcheck?chk=1")
        }
    }

    fun BasketListInitialize(){

        val service = RetrofitClientInstance.retrofitInstance?.create(BasketProductService::class.java);
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

                val paytext = getString(R.string.pay);
                bp_check.clear()
                basket_products.removeAllViews()

                for (i in list!!){
                    val bp = LayoutInflater.from(applicationContext).inflate(R.layout.basket_product, basket_products, false);
                    basket_products.addView(bp)
                    bp.bp_textView_pay.setText(paytext+" : "+i.pay.toString()+" KRW")
                    bp.bp_textView_pname.setText(i.name)
                    bp.bp_editText_num.setText(i.num.toString())
                    bp.bp_editText_num.addTextChangedListener(object : TextWatcher{//갯수 에딧텍스트가 변경되면 DB 업데이트를 시전한다.
                        override fun afterTextChanged(s: Editable?) {
                            Log.d("url :: ", IP+"/basketupdate?num="+s+"&p="+i.product_index+"&flag=1")
                            wb.loadUrl(IP+"/basketupdate?num="+s+"&p="+i.product_index+"&flag=1")
                        }
                        override fun beforeTextChanged(s: CharSequence?,start: Int,count: Int,after: Int) {}
                        override fun onTextChanged(s: CharSequence?,start: Int,before: Int,count: Int) {}

                    })
                    bp.bp_checkBox.isChecked=(i.checked == 1)
                    bp.bp_checkBox.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{//체크하면 DB에서 체크 데이터가 갱신된다.
                        override fun onCheckedChanged(arg0:CompoundButton, arg1:Boolean){
                            Log.d("url :: ", IP+"/basketupdatecheck?chk=" + (if (bp.bp_checkBox.isChecked) "1" else "0") + "&p=" + i.product_index)
                            wb.loadUrl(IP+"/basketupdatecheck?chk=" + (if (bp.bp_checkBox.isChecked) "1" else "0") + "&p=" + i.product_index)
                        }
                    })
                    bp_check.add(bp.bp_checkBox)
                    bp.deletebutton.setOnClickListener(object : View.OnClickListener {//삭제버튼 이벤트
                        override fun onClick(v: View?) {
                            Log.d("url :: ", IP+"/basketdelete?b_productindex="+i.product_index)
                            wb.loadUrl(IP+"/basketdelete?b_productindex="+i.product_index)
                        }
                    })

                    val imgurl = Uri.parse("http://192.168.55.231:8080"+i.img)
                    Glide.with(applicationContext).load(imgurl).into(bp.imageView_bp);
                }
                Log.i("bp_check :: ", bp_check.toString())
                val totaltextstart=getString(R.string.bottom_menu_totalpay)
                val totaltext = " : "+body?.total
                textView_totalpay.setText(totaltextstart+totaltext)

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
                return true;
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        return ActivityTransferManager.startActivityByBottomTabClick(this, R.id.basket, p0.itemId)
    }
}



