package com.example.myparcelapp.view

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
    var IP=""
    var basketAdded:Int = 0
    var BuyingProduct_index:String = ""
    var BuyingProduct_num:Int = 0
    var BuyingProduct_title:String = ""
    var BuyingProduct_image:String = ""
    var BuyingProduct_total:Int = 0
    var BuyingProduct_pay:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        IP = resources.getString(R.string.homepageIP)
        pid = intent.getStringExtra("pid")
        productInitialize(pid, this)
        wb = WebView(this)
        wb.loadUrl(IP+"/sessiontest/")
        wb.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                productInitialize_one(pid)
            }
        }
        //wb.loadUrl("http://192.168.55.231:8080/sessiontest/")
        //로그인 해결되기 전까진 이렇게 한다.
    }


    fun productInitialize(pid:String, activity: Activity){

        val service = RetrofitClientInstance.retrofitInstance?.create(ProductPageService::class.java)
        val call = service?.productlist(pid,resources.getString(R.string.temporarilyUsercode))
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
                Log.d("Response :: ", response?.toString())
                val body = response?.body()
                val list = body?.prdlist
                var size = list?.size
                Log.i("list :: ", list.toString())
                val paytext = getString(R.string.pay)

                bp_textView_name.setText(list?.get(0)?.name)
                bp_textView_pay.setText(list?.get(0)?.pay)
                textView_inf.setText(list?.get(0)?.explanatory)
                star.progress= list!![0].star
                BuyingProduct_index = list!![0].index
                BuyingProduct_num = 1
                BuyingProduct_title=list?.get(0)?.name
                BuyingProduct_pay = list?.get(0)?.pay.toInt()

                basketAdded = list!![0].basketed
                if(basketAdded == 1){
                    putthebasketButton.setText(resources.getString(R.string.putthebasketButton_off))//장바구니에 저장됨
                    putthebasketButton.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            Log.d("url :: ", IP+"/product_basket_delete?p="+pid)
                            wb.loadUrl(IP+"/product_basket_delete?p="+pid)
                        }
                    })
                }
                else{
                    putthebasketButton.setText(resources.getString(R.string.putthebasketButton))//장바구니에 넣기
                    putthebasketButton.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            Log.d("url :: ", IP+"/product_basket_add?p="+pid+"&num="+bp_editText_num.text)
                            wb.loadUrl(IP+"/product_basket_add?p="+pid+"&num="+bp_editText_num.text)
                        }
                    })
                }

                Log.d("star.progress :: ", star.progress.toString())

                prevealimagebig.removeAllViews()
                body?.pdimages.forEach{
                    var imgv = ImageView(applicationContext)
                    val imgwidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        320F, getResources().getDisplayMetrics()).toInt()
                    val imgheight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        320F, getResources().getDisplayMetrics()).toInt()
                    imgv.layoutParams= ViewGroup.LayoutParams(imgwidth,imgheight)
                    val imgurl = Uri.parse(IP+it)
                    Glide.with(applicationContext).load(imgurl).into(imgv)
                    prevealimagebig.addView(imgv)
                    Log.d("i :: ", it.toString())
                }
                BuyingProduct_image = IP+body?.pdimages[0]


                var sametagProductlist = ArrayList<ProductVO>()
                body?.prdlist_sametag.forEach{
                    sametagProductlist.add(it)
                }
                sametagProductlist.removeAt(sametagProductlist.size - 1)
                var arr2 =  HashSet<ProductVO>(sametagProductlist)
                sametagProductlist.clear()
                sametagProductlist = ArrayList<ProductVO>(arr2)


                var samebrandProductlist = ArrayList<ProductVO>()
                body?.prdlist_samebrand.forEach{
                    samebrandProductlist.add(it)
                }
                var arr3 =  HashSet<ProductVO>(samebrandProductlist)
                samebrandProductlist.clear()
                samebrandProductlist = ArrayList<ProductVO>(arr3)
                layout_tag.removeAllViews()
                layout_brand.removeAllViews()

                sametagProductlist.forEach{//같은 태그 상품
                    val td = LayoutInflater.from(applicationContext).inflate(R.layout.standard_product, layout_tag, false)
                    val oc =
                        ProductPageOpenOnClick(
                            activity,
                            applicationContext,
                            it.index
                        )
                    layout_tag.addView(td)
                    td.td_name.text = it.name.toString()
                    td.td_pay.text = paytext+" : "+it.pay.toString()+"KRW"
                    td.td_star.progress=it.star
                    td.layout_bp.setOnClickListener(oc)

                    val imgurl = Uri.parse(IP+it.img)
                    Glide.with(applicationContext).load(imgurl).into(td.imageView_td)
                    Log.d("i :: ", it.toString())
                }

                samebrandProductlist.forEach{//같은 브랜드 상품
                    val td = LayoutInflater.from(applicationContext).inflate(R.layout.standard_product, layout_brand, false)
                    val oc =
                        ProductPageOpenOnClick(
                            activity,
                            applicationContext,
                            it.index
                        )
                    td.layout_bp.setOnClickListener(oc)
                    layout_brand.addView(td)
                    td.td_name.text=it.name.toString()
                    td.td_pay.text = paytext+" : "+it.pay.toString()+"KRW"
                    td.td_star.progress=it.star


                    val imgurl = Uri.parse(IP+it.img)
                    Glide.with(applicationContext).load(imgurl).into(td.imageView_td)
                    Log.d("i :: ", it.toString())
                }

                //val imgurl = Uri.parse("http://192.168.55.231:8080"+i.img)
                //Glide.with(applicationContext).load(imgurl).into(td.imageView_td)

                Log.d("body :: ", body.toString())
                Log.d("list :: ", list.toString())
                Log.d("size :: ", size.toString())


            }

        })
    }
    fun productInitialize_one(pid:String){
        productInitialize(pid, this)
    }

    fun onclickputstar(v: View){
        val okbuttontext = resources.getString(R.string.putstarokbutton)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alert_putstar, null)

        val alertDialog = AlertDialog.Builder(this)
            .setPositiveButton(okbuttontext){
                dialog, which ->
                var star = "0"
                star = when (true){
                    view.radioButton1.isChecked -> 1
                    view.radioButton2.isChecked -> 2
                    view.radioButton3.isChecked -> 3
                    view.radioButton4.isChecked -> 4
                    view.radioButton5.isChecked -> 5
                    else -> 0
                }.toString()
                Log.d("url :: ", IP+"/starupdate?id="+ pid+"&num="+star)
                wb.loadUrl(IP+"/starupdate?id="+pid+"&num="+star)

            }
            .setNegativeButton(resources.getString(R.string.cancel),null)
            .create()
        alertDialog.setView(view)
        alertDialog.show()
    }

    
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onclick_Buy(v: View) {
        var buyIndexlist = ArrayList<String>()
        var buyNumlist = ArrayList<Int>()
        var buyTitlelist = ArrayList<String>()
        var buyImagelist = ArrayList<String>()
        BuyingProduct_num = bp_editText_num.text.toString().toInt()
        buyIndexlist.add( BuyingProduct_index )
        buyNumlist.add( BuyingProduct_num )
        buyTitlelist.add( BuyingProduct_title )
        buyImagelist.add( BuyingProduct_image )
        BuyingProduct_total = BuyingProduct_num * BuyingProduct_pay
        startActivityBuyButtonClick(this, buyIndexlist, buyNumlist, buyTitlelist, buyImagelist, BuyingProduct_total)
    }
    //바로 구매
    //상품 페이지에서는 한 종류밖에 주문할 수 없다.

}
