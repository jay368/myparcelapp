package com.example.myparcelapp

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.myparcelapp.dto.ProductVOList
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_searchresult.*
import kotlinx.android.synthetic.main.layout_category.*
import kotlinx.android.synthetic.main.layout_home.*
import kotlinx.android.synthetic.main.layout_search.view.*
import kotlinx.android.synthetic.main.standard_product.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : Activity() , BottomNavigationView.OnNavigationItemSelectedListener{

    lateinit var search_button_event:SearchButton
    var IP=""

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        IP = resources.getString(R.string.homepageIP)

        include_home.visibility=View.VISIBLE
        val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.selectedItemId=R.id.home
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        val wb: WebView = WebView(this)
        wb.loadUrl(IP+"/sessiontest/")
        //로그인 해결되기 전까진 이렇게 한다.
        search_button_event = SearchButton(this,
                                            applicationContext,
                                            include_search.spinner_searchfilter.selectedItemId.toString(),
                                            resources.getStringArray(R.array.productkind)[0].toString())
        include_search.searchview1.isSubmitButtonEnabled = true;
        include_search.searchview1.setOnQueryTextListener(search_button_event)
        searchview1.isSubmitButtonEnabled = true;
        searchview1.setOnQueryTextListener(search_button_event)

        spinner_searchfilter.onItemSelectedListener= object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                search_button_event.SetOptions_flt(position.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        LayoutsLoad()
        TodayDealInitialize(this)
    }

    fun StartActivitySearchresultActivity(query:String, flt:String){
        val intent = Intent(applicationContext, SearchresultActivity::class.java)
        intent.putExtra("sch",query);//검색단어
        intent.putExtra("flt",flt);//필터
        intent.putExtra("st","0");//몇 별점 이상이여야만 검색되는가
        intent.putExtra("tag","");//태그
        intent.putExtra("br","");//브랜드
        intent.putExtra("agn","0");//정렬
        val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
        bottomNavigationView.selectedItemId=R.id.home
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        include_category.visibility=View.GONE
        include_search.visibility=View.GONE
        include_home.visibility=View.GONE
        when(p0.itemId){
            R.id.category ->{
                include_category.visibility=View.VISIBLE
                return true;
            }
            R.id.search -> {
                include_search.visibility=View.VISIBLE
                return true;
            }
            R.id.home -> {
                include_search.spinner_searchfilter.setSelection(0)
                include_home.visibility=View.VISIBLE
                TodayDealInitialize(this)
                return true;
            }
            R.id.basket -> {
                val intent = Intent(this, BasketActivity::class.java)
                val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
                bottomNavigationView.selectedItemId=R.id.home
                startActivity(intent , ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                return true;
            }
            R.id.order -> {
                val intent2 = Intent(this, Order_Activity::class.java)
                val bottomNavigationView : BottomNavigationView = navigationView as BottomNavigationView
                bottomNavigationView.selectedItemId=R.id.home
                startActivity(intent2 , ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
                return true;
            }
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun LayoutsLoad(){//카테고리 레이아웃
        table_category.removeAllViews()
        var categorystr:Array<String> = resources.getStringArray(R.array.productkind);

        var strindex=0;
        var i = 0
        while (true){
            var row = TableRow(this);
            row.layoutParams= TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f)
            row.gravity = Gravity.FILL
            row.textAlignment = View.TEXT_ALIGNMENT_CENTER
            for (j in 0 ..3){
                var newbt = Button(this)
                newbt.setText(categorystr.get(strindex))
                newbt.setTextSize(15F)
                newbt.layoutParams= TableRow.LayoutParams(0, 180, 1f)
                row.addView(newbt)

                strindex+=1
                if(strindex>=categorystr.size)break
            }
            table_category.addView(row)
            if(strindex>=categorystr.size)break
            i++
        }
    }






    fun TodayDealInitialize(activity:Activity){

        val service = RetrofitClientInstance.retrofitInstance?.create(TodayDealService::class.java);
        val call = service?.todaydeallist()
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
                todaydeallist.removeAllViews()
                Log.d("Response :: ", response?.toString())
                val body = response?.body()
                val list = body?.prdlist
                var size = list?.size
                Log.i("list :: ", list.toString())
                val paytext = getString(R.string.pay);

                //
                for (i in list!!){
                    val td = LayoutInflater.from(applicationContext).inflate(R.layout.standard_product, todaydeallist, false);
                    val oc:ProductPageOpenOnClick = ProductPageOpenOnClick(activity, applicationContext, i.index)
                    todaydeallist.addView(td)
                    td.td_name.setText(i.name.toString())
                    td.td_pay.setText(paytext+" : "+i.pay.toString()+"KRW")
                    td.td_star.progress=i.star;
                    td.layout_bp.setOnClickListener(oc)

                    val imgurl = Uri.parse(IP+i.img)
                    Glide.with(applicationContext).load(imgurl).into(td.imageView_td);
                    Log.d("i :: ", i.toString())
                }


                Log.d("body :: ", body.toString())
                Log.d("list :: ", list.toString())
                Log.d("size :: ", size.toString())


            }

        })
    }


    fun ProductPageOpen(index:String){
        val intent = Intent(applicationContext, ProductActivity::class.java)
        intent.putExtra("pid", index)
        startActivity(intent)
    }


}


