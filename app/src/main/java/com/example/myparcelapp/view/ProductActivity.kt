package com.example.myparcelapp.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.myparcelapp.events.ProductPageOpenOnClick
import com.example.myparcelapp.R
import com.example.myparcelapp.events.PutStarButton
import com.example.myparcelapp.utils.RetrofitClientInstance
import com.example.myparcelapp.model.ProductVO
import com.example.myparcelapp.model.ProductVOList
import com.example.myparcelapp.service.ProductPageService
import com.example.myparcelapp.utils.ActivityTransferManager.startActivityBuyButtonClick
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.alert_putstar.view.*
import kotlinx.android.synthetic.main.standard_product.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductActivity : Activity() {
    lateinit var wb:WebView
    lateinit var pid:String
    var ip=""
    var basketAdded:Int = 0
    var buyingProductIndex:String = ""
    var buyingProductNum:Int = 0
    var buyingProductTitle:String = ""
    var buyingProductImage:String = ""
    private var buyingProductTotal:Int = 0
    var buyingProductPay:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        ip = resources.getString(R.string.homepageIP)
        pid = intent.getStringExtra("pid")
        productInitialize(pid, this)
        wb = WebView(this)
        wb.loadUrl("$ip/sessiontest/")
        putStarButton.setOnClickListener(PutStarButton(
            this,
            this,
            resources,
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
            ip,
            pid,
            wb)
        )
        wb.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                productInitializeOne(pid)
            }
        }
        //wb.loadUrl("http://192.168.55.231:8080/sessiontest/")
        //로그인 해결되기 전까진 이렇게 한다.
    }


    private fun productInitialize(pid:String, activity: Activity){

        val service = RetrofitClientInstance.retrofitInstance?.create(ProductPageService::class.java)
        val call = service?.productList(pid,resources.getString(R.string.temporarilyUsercode))
        Log.d("service :: ", service?.toString())
        Log.d("call :: ", call?.toString())


        call?.enqueue(object : Callback<ProductVOList> {
            override fun onFailure(call: Call<ProductVOList>, t: Throwable) {
                Log.d("Error :: ", t.toString())
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ProductVOList>?,
                response: Response<ProductVOList>?
            ) {
                Log.d("Response :: ", response?.toString())
                val body = response?.body()
                val list = body?.prdlist
                val size = list?.size
                Log.i("list :: ", list.toString())
                val payText = getString(R.string.pay)

                bp_textView_name.text = list?.get(0)?.name
                bp_textView_pay.text = list?.get(0)?.pay
                textView_inf.text = list?.get(0)?.explanatory
                star.progress = list!![0].star
                buyingProductIndex = list[0].index
                buyingProductNum = 1
                buyingProductTitle = list[0].name
                buyingProductPay = list[0].pay.toInt()

                basketAdded = list[0].basketed
                if(basketAdded == 1){
                    putthebasketButton.text = resources.getString(R.string.putTheBasketButton_off)//장바구니에 저장됨
                    putthebasketButton.setOnClickListener {
                        Log.d("url :: ", "$ip/product_basket_delete?p=$pid")
                        wb.loadUrl("$ip/product_basket_delete?p=$pid")
                    }
                }
                else{
                    putthebasketButton.text = resources.getString(R.string.putTheBasketButton)//장바구니에 넣기
                    putthebasketButton.setOnClickListener {
                        Log.d("url :: ", "$ip/product_basket_add?p=$pid&num=${bp_editText_num.text}")
                        wb.loadUrl("$ip/product_basket_add?p=$pid&num=${bp_editText_num.text}")
                    }
                }

                Log.d("star.progress :: ", star.progress.toString())

                prevealimagebig.removeAllViews()
                body.pdimages.forEach{
                    val imgView = ImageView(applicationContext)
                    val imgWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        320F, resources.displayMetrics
                    ).toInt()
                    val imgHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        320F, resources.displayMetrics
                    ).toInt()
                    imgView.layoutParams = ViewGroup.LayoutParams(imgWidth,imgHeight)
                    val imgUrl = Uri.parse("$ip$it")
                    Glide.with(applicationContext).load(imgUrl).into(imgView)
                    prevealimagebig.addView(imgView)
                    Log.d("i :: ", it)
                }
                buyingProductImage = "$ip${body.pdimages[0]}"


                var sameTagProductList = ArrayList<ProductVO>()
                body.prdlist_sametag.forEach{
                    sameTagProductList.add(it)
                }
                sameTagProductList.removeAt(sameTagProductList.size - 1)
                val arr2 = HashSet<ProductVO>(sameTagProductList)
                sameTagProductList.clear()
                sameTagProductList = ArrayList<ProductVO>(arr2)


                var sameBrandProductList = ArrayList<ProductVO>()
                body.prdlist_samebrand.forEach{
                    sameBrandProductList.add(it)
                }
                val arr3 =  HashSet<ProductVO>(sameBrandProductList)
                sameBrandProductList.clear()
                sameBrandProductList = ArrayList<ProductVO>(arr3)
                layout_tag.removeAllViews()
                layout_brand.removeAllViews()

                sameTagProductList.forEach{//같은 태그 상품
                    val td = LayoutInflater.from(applicationContext).inflate(R.layout.standard_product, layout_tag, false)
                    val oc =
                        ProductPageOpenOnClick(
                            activity,
                            it.index
                        )
                    layout_tag.addView(td)
                    td.td_name.text = it.name
                    td.td_pay.text = "$payText : ${it.pay} KRW"
                    td.td_star.progress = it.star
                    td.layout_bp.setOnClickListener(oc)

                    val imgUrl = Uri.parse("$ip${it.img}")
                    Glide.with(applicationContext).load(imgUrl).into(td.imageView_td)
                    Log.d("i :: ", it.toString())
                }

                sameBrandProductList.forEach{//같은 브랜드 상품
                    val td = LayoutInflater.from(applicationContext).inflate(R.layout.standard_product, layout_brand, false)
                    val oc =
                        ProductPageOpenOnClick(
                            activity,
                            it.index
                        )
                    td.layout_bp.setOnClickListener(oc)
                    layout_brand.addView(td)
                    td.td_name.text = it.name
                    td.td_pay.text = "$payText : ${it.pay} KRW"
                    td.td_star.progress = it.star


                    val imgUrl = Uri.parse("$ip${it.img}")
                    Glide.with(applicationContext).load(imgUrl).into(td.imageView_td)
                    Log.d("i :: ", it.toString())
                }

                Log.d("body :: ", body.toString())
                Log.d("list :: ", list.toString())
                Log.d("size :: ", size.toString())


            }

        })
    }
    fun productInitializeOne(pid:String){
        productInitialize(pid, this)
    }

    
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onclickBuy(v: View) {
        val buyIndexList = ArrayList<String>()
        val buyNumList = ArrayList<Int>()
        val buyTitleList = ArrayList<String>()
        val buyImageList = ArrayList<String>()
        buyingProductNum = bp_editText_num.text.toString().toInt()
        buyIndexList.add( buyingProductIndex )
        buyNumList.add( buyingProductNum )
        buyTitleList.add( buyingProductTitle )
        buyImageList.add( buyingProductImage )
        buyingProductTotal = buyingProductNum * buyingProductPay
        startActivityBuyButtonClick(this, buyIndexList, buyNumList, buyTitleList, buyImageList, buyingProductTotal)
    }
    //바로 구매
    //상품 페이지에서는 한 종류밖에 주문할 수 없다.

}
