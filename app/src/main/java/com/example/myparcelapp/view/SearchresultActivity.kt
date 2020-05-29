package com.example.myparcelapp.view

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
    lateinit var searchview_Searchresult:SearchView
    lateinit var search_button_event: SearchButton
    var IP=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchresult)
        IP = resources.getString(R.string.homepageIP)


        val intent = getIntent()
        searchResultListInitialize(intent.getStringExtra("sch"),
                                    intent.getStringExtra("flt"),
                                    intent.getStringExtra("st"),
                                    intent.getStringExtra("tag"),
                                    intent.getStringExtra("br"),
                                    intent.getStringExtra("agn"),
                                    this)
        search_button_event = SearchButton(
            this,
            this,
            "0",
            resources.getStringArray(R.array.productkind)[0].toString()
        )
        search_button_event.setOptions_flt(intent.getStringExtra("flt"))
        search_button_event.setOptions_st(intent.getStringExtra("st"))
        search_button_event.setOptions_tag(intent.getStringExtra("tag"))
        search_button_event.setOptions_br(intent.getStringExtra("br"))
        search_button_event.setOptions_agn(intent.getStringExtra("agn"))
        spinner_searchfilter.setSelection(intent.getStringExtra("flt").toInt())
        spinner_searchfilter.onItemSelectedListener= object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                search_button_event.setOptions_flt(position.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinner_tag.onItemSelectedListener= object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View,position: Int,id: Long) {
                search_button_event.setOptions_tag(spinner_tag.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinner_brand.onItemSelectedListener= object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?,view: View,position: Int,id: Long) {
                search_button_event.setOptions_br(spinner_brand.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        ratingBar2.onRatingBarChangeListener = object : RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(ratingBar: RatingBar?,rating: Float,fromUser: Boolean) {
                search_button_event?.setOptions_st(rating.toInt().toString())
            }

        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        searchview_Searchresult = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchview_Searchresult.maxWidth=Integer.MAX_VALUE
        searchview_Searchresult.queryHint=resources.getString(R.string.searchhint)
        searchview_Searchresult.isSubmitButtonEnabled = true
        searchview_Searchresult.setOnQueryTextListener(search_button_event)
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
        val call = service?.searchresultlist(sch, flt, st, tag, br, agn)
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
                searchresultview.removeAllViews()
                Log.d("Response :: ", response?.toString())
                val body = response?.body()
                val list = body?.prdlist
                var size = list?.size
                Log.i("list :: ", list.toString())
                val paytext = getString(R.string.pay)

                Log.d("listsize :: ", list?.size.toString())
                list!!.forEach{
                    val td = LayoutInflater.from(applicationContext).inflate(R.layout.standard_product, searchresultview, false)
                    val oc =
                        ProductPageOpenOnClick(
                            activity,
                            applicationContext,
                            it.index
                        )
                    searchresultview.addView(td)
                    td.td_name.setText(it.name.toString())
                    td.td_pay.setText(paytext+" : "+it.pay.toString()+"KRW")
                    td.td_star.progress=it.star
                    td.layout_bp.setOnClickListener(oc)

                    val imgurl = Uri.parse(IP+it.img)
                    Glide.with(applicationContext).load(imgurl).into(td.imageView_td)
                    Log.d("i :: ", it.toString())
                }
                val list_tag = body?.tag
                val list_brand = body?.brand

                val tag_items = ArrayAdapter<String>(applicationContext,
                    R.layout.spinner_item
                )
                val brand_items = ArrayAdapter<String>(applicationContext,
                    R.layout.spinner_item
                )
                tag_items.add(resources.getStringArray(R.array.productkind)[0])
                list_tag.forEach {
                    tag_items.add(it)
                }
                brand_items.add(resources.getStringArray(R.array.productkind)[0])
                list_brand.forEach {
                    brand_items.add(it)
                }

                spinner_tag.adapter=tag_items
                spinner_brand.adapter=brand_items


                Log.d("body :: ", body.toString())
                Log.d("list :: ", list.toString())
                Log.d("size :: ", size.toString())


            }

        })
    }

}
