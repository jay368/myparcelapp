package com.example.myparcelapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.myparcelapp.dto.ProductVO
import com.example.myparcelapp.dto.ProductVOList
import com.example.myparcelapp.service.ProductPageService
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        IP = resources.getString(R.string.homepageIP)
        pid = intent.getStringExtra("pid")
        ProductInitialize(pid, this)
        wb = WebView(this)
        wb.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view:WebView, url:String) {
                super.onPageFinished(view, url)
                ProductInitialize_one(pid)
            }
        });
        //wb.loadUrl("http://192.168.55.231:8080/sessiontest/")
        //로그인 해결되기 전까진 이렇게 한다.
    }


    fun ProductInitialize(pid:String, activity: Activity){

        val service = RetrofitClientInstance.retrofitInstance?.create(ProductPageService::class.java);
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
                val paytext = getString(R.string.pay);

                bp_textView_name.setText(list?.get(0)?.name)
                bp_textView_pay.setText(list?.get(0)?.pay)
                textView_inf.setText(list?.get(0)?.explanatory)
                star.progress= list!![0].star

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
                for (i in body?.pdimages){
                    var imgv = ImageView(applicationContext)
                    val imgwidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        320F, getResources().getDisplayMetrics()).toInt()
                    val imgheight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        320F, getResources().getDisplayMetrics()).toInt()
                    imgv.layoutParams= ViewGroup.LayoutParams(imgwidth,imgheight)
                    val imgurl = Uri.parse(IP+i)
                    Glide.with(applicationContext).load(imgurl).into(imgv);
                    prevealimagebig.addView(imgv)
                    Log.d("i :: ", i.toString())
                }


                var sametagProductlist = ArrayList<ProductVO>()
                for (i in body?.prdlist_sametag){
                    sametagProductlist.add(i)
                }
                sametagProductlist.removeAt(sametagProductlist.size - 1)
                var arr2 =  HashSet<ProductVO>(sametagProductlist)
                sametagProductlist.clear()
                sametagProductlist = ArrayList<ProductVO>(arr2)


                var samebrandProductlist = ArrayList<ProductVO>()
                for (i in body?.prdlist_samebrand){
                    samebrandProductlist.add(i)
                }
                var arr3 =  HashSet<ProductVO>(samebrandProductlist)
                samebrandProductlist.clear()
                samebrandProductlist = ArrayList<ProductVO>(arr3)
                layout_tag.removeAllViews()
                layout_brand.removeAllViews()

                for (i in sametagProductlist){
                    val td = LayoutInflater.from(applicationContext).inflate(R.layout.standard_product, layout_tag, false);
                    val oc = ProductPageOpenOnClick(activity, applicationContext, i.index)
                    layout_tag.addView(td)
                    td.td_name.setText(i.name.toString())
                    td.td_pay.setText(paytext+" : "+i.pay.toString()+"KRW")
                    td.td_star.progress=i.star;
                    td.layout_bp.setOnClickListener(oc)

                    val imgurl = Uri.parse(IP+i.img)
                    Glide.with(applicationContext).load(imgurl).into(td.imageView_td);
                    Log.d("i :: ", i.toString())
                }

                for (i in samebrandProductlist){
                    val td = LayoutInflater.from(applicationContext).inflate(R.layout.standard_product, layout_brand, false);
                    val oc = ProductPageOpenOnClick(activity, applicationContext, i.index)
                    layout_brand.addView(td)
                    td.td_name.setText(i.name.toString())
                    td.td_pay.setText(paytext+" : "+i.pay.toString()+"KRW")
                    td.td_star.progress=i.star;
                    td.layout_bp.setOnClickListener(oc)

                    val imgurl = Uri.parse(IP+i.img)
                    Glide.with(applicationContext).load(imgurl).into(td.imageView_td);
                    Log.d("i :: ", i.toString())
                }

                //val imgurl = Uri.parse("http://192.168.55.231:8080"+i.img)
                //Glide.with(applicationContext).load(imgurl).into(td.imageView_td);

                Log.d("body :: ", body.toString())
                Log.d("list :: ", list.toString())
                Log.d("size :: ", size.toString())


            }

        })
    }
    fun ProductInitialize_one(pid:String){
        ProductInitialize(pid, this)
    }

    fun AnotherProduct(index:String){
        ProductInitialize(index, this)
    }

    fun onclickputstar(v: View){
        val okbuttontext = resources.getString(R.string.putstarokbutton)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.alert_putstar, null)

        val alertDialog = AlertDialog.Builder(this)
            .setPositiveButton(okbuttontext){
                dialog, which ->
                var star = "0"
                if (view.radioButton1.isChecked){
                    star = "1"
                }else if (view.radioButton2.isChecked){
                    star = "2"
                }else if (view.radioButton3.isChecked){
                    star = "3"
                }else if (view.radioButton4.isChecked){
                    star = "4"
                }else if (view.radioButton5.isChecked){
                    star = "5"
                }
                Log.d("url :: ", IP+"/starupdate?id="+ pid+"&num="+star)
                wb.loadUrl(IP+"/starupdate?id="+pid+"&num="+star)

            }
            .setNegativeButton(resources.getString(R.string.cancel),null)
            .create()
        alertDialog.setView(view)
        alertDialog.show()
    }


}
