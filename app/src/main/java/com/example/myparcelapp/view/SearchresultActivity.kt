package com.example.myparcelapp.view

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myparcelapp.events.ProductPageOpenOnClick
import com.example.myparcelapp.R
import com.example.myparcelapp.utils.RetrofitClientInstance
import com.example.myparcelapp.events.SearchButton
import com.example.myparcelapp.model.ProductVOList
import com.example.myparcelapp.service.TodayDealService
import kotlinx.android.synthetic.main.activity_searchresult.*
import kotlinx.android.synthetic.main.standard_product.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchresultActivity : AppCompatActivity() {
    private lateinit var searchViewSearchResult:SearchView
    lateinit var searchButtonEvent: SearchButton
    var ip = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchresult)
        ip = resources.getString(R.string.homepageIP)


        val intent = intent
        searchResultListInitialize(intent.getStringExtra("sch"),
                                    intent.getStringExtra("flt"),
                                    intent.getStringExtra("st"),
                                    intent.getStringExtra("tag"),
                                    intent.getStringExtra("br"),
                                    intent.getStringExtra("agn"),
                                    this)
        searchButtonEvent = SearchButton(
            this,
            this,
            "0",
            resources.getStringArray(R.array.productKind)[0].toString()
        )
        searchButtonEvent.setOptionsFlt(intent.getStringExtra("flt"))
        searchButtonEvent.setOptionsStar(intent.getStringExtra("st"))
        searchButtonEvent.setOptionsTag(intent.getStringExtra("tag"))
        searchButtonEvent.setOptionsBrand(intent.getStringExtra("br"))
        searchButtonEvent.setOptionsAlign(intent.getStringExtra("agn"))
        spinner_searchfilter.setSelection(intent.getStringExtra("flt").toInt())
        spinner_searchfilter.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                searchButtonEvent.setOptionsFlt(position.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinner_tag.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View,position: Int,id: Long) {
                searchButtonEvent.setOptionsTag(spinner_tag.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinner_brand.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View,position: Int,id: Long) {
                searchButtonEvent.setOptionsBrand(spinner_brand.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        ratingBar2.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { ratingBar, rating, fromUser -> searchButtonEvent.setOptionsStar(rating.toInt().toString()) }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        searchViewSearchResult = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchViewSearchResult.maxWidth = Integer.MAX_VALUE
        searchViewSearchResult.queryHint = resources.getString(R.string.searchHint)
        searchViewSearchResult.isSubmitButtonEnabled = true
        searchViewSearchResult.setOnQueryTextListener(searchButtonEvent)
        return true
    }


    fun searchResultListInitialize(sch:String, flt:String, st:String, tag:String, br:String, agn:String,activity:Activity){
        Log.d("sch :: ", sch)
        Log.d("flt :: ", flt)
        Log.d("st :: ", st)
        Log.d("tag :: ", tag)
        Log.d("br :: ", br)
        Log.d("agn :: ", agn)

        val service = RetrofitClientInstance.retrofitInstance?.create(TodayDealService::class.java)
        val call = service?.searchResultList(sch, flt, st, tag, br, agn)
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
                searchresultview.removeAllViews()
                Log.d("Response :: ", response?.toString())
                val body = response?.body()
                val list = body?.prdlist
                var size = list?.size
                Log.i("list :: ", list.toString())
                val paytext = getString(R.string.pay)

                Log.d("list_size :: ", list?.size.toString())
                list!!.forEach{
                    val td = LayoutInflater.from(applicationContext).inflate(R.layout.standard_product, searchresultview, false)
                    val oc =
                        ProductPageOpenOnClick(
                            activity,
                            it.index
                        )
                    searchresultview.addView(td)
                    td.td_name.text = it.name
                    td.td_pay.text = "$paytext : ${it.pay} KRW"
                    td.td_star.progress=it.star
                    td.layout_bp.setOnClickListener(oc)

                    val imgUrl = Uri.parse(ip+it.img)
                    Glide.with(applicationContext).load(imgUrl).into(td.imageView_td)
                    Log.d("i :: ", it.toString())
                }
                val listTag = body.tag
                val listBrand = body.brand

                val tagItems = ArrayAdapter<String>(applicationContext,
                    R.layout.spinner_item
                )
                val brandItems = ArrayAdapter<String>(applicationContext,
                    R.layout.spinner_item
                )
                tagItems.add(resources.getStringArray(R.array.productKind)[0])
                listTag.forEach {
                    tagItems.add(it)
                }
                brandItems.add(resources.getStringArray(R.array.productKind)[0])
                listBrand.forEach {
                    brandItems.add(it)
                }

                spinner_tag.adapter = tagItems
                spinner_brand.adapter = brandItems


                Log.d("body :: ", body.toString())
                Log.d("list :: ", list.toString())
                Log.d("size :: ", size.toString())


            }

        })
    }

}
