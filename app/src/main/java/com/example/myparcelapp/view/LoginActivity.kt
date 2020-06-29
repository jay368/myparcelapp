package com.example.myparcelapp.view

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.myparcelapp.MyApplication
import com.example.myparcelapp.R
import com.example.myparcelapp.model.MobileLoginVOList
import com.example.myparcelapp.model.OrderVOList
import com.example.myparcelapp.model.UserVO
import com.example.myparcelapp.service.MobileLoginService
import com.example.myparcelapp.service.OrderService
import com.example.myparcelapp.service.UserService
import com.example.myparcelapp.utils.ActivityTransferManager
import com.example.myparcelapp.utils.RetrofitClientInstance
import com.example.myparcelapp.utils.Uuid
import com.example.myparcelapp.utils.Uuid.userIndex
import kotlinx.android.synthetic.main.activity_buy.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_order_.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.Charset
import java.util.Base64

class LoginActivity : Activity() {
    lateinit var wb: WebView
    private lateinit var ip:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginInitialize(this)

        ip = resources.getString(R.string.homepageIP)
    }

    private fun loginInitialize(context: Context) {
        val service = RetrofitClientInstance.retrofitInstance?.create(MobileLoginService::class.java)
        val call = service?.mobileLoginList(Uuid.getUuid(context))

        call?.enqueue(object : Callback<MobileLoginVOList> {
            override fun onFailure(call: Call<MobileLoginVOList>, t: Throwable) {
                Log.d("Error :: ", t.toString())
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onResponse(call: Call<MobileLoginVOList>?, response: Response<MobileLoginVOList>?) {
                Log.d(MyApplication.LogTag, "Response :: ${response?.toString()}")

                response
                    ?.takeIf { it.isSuccessful }
                    ?.let {
                        it.body()?.ml?.forEach { ml_data->
                            when(ml_data.uuid){
                                Uuid.getUuid(context)-> {
                                    userIndex = ml_data.index
                                    loginComplete()
                                }
                            }
                            //이미 로그인 되어있으면?(Uuid.getUuid(context)->)
                            //로그인 된 상태가 아니라면?(else)
                        }
                    }

                val body = response?.body()
                val list = body?.ml
                val size = list?.size
                Log.i("list :: ", list.toString())
                Log.d("body :: ", body.toString())
                Log.d("list :: ", list.toString())
                Log.d("size :: ", size.toString())


            }

        })
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun loginComplete(){
        ActivityTransferManager.startActivityMain(this)//메인으로 이동
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun loginButton(v: View){
        var encoder = Base64.getEncoder()
        var id = encoder.encode(editTextTextEmailAddress.text.toString().toByteArray())
        var pwd = encoder.encode(editTextTextPassword.text.toString().toByteArray())


        val service = RetrofitClientInstance.retrofitInstance?.create(UserService::class.java)
        val call = service?.user(String(id),"1")
        //flag가 1이면 이메일을 읽게 한다.

        call?.enqueue(object : Callback<UserVO> {
            override fun onFailure(call: Call<UserVO>, t: Throwable) {
                Log.d("Error :: ", t.toString())
            }

            override fun onResponse(call: Call<UserVO>?, response: Response<UserVO>?
            ) {
                val body = response?.body()
                when (body?.email){
                    String(id) ->
                        when (body?.pwd){
                            String(pwd) -> {
                                userIndex = body?.index
                                wb.loadUrl("$ip/mobilelogin_insert?uindex=${body?.index}&uuid=${Uuid.getUuid(getContext())}")
                                Log.d("url :: ","$ip/mobilelogin_insert?uindex=${body?.index}&uuid=${Uuid.getUuid(getContext())}")
                                messageShow("로그인 완료")
                                //로그인이 완료되었다는 메시지
                                loginComplete()
                                //이메일,비번이 모두 맞을 경우
                            }
                            else -> messageShow("패스워드가 맞지 않습니다.")

                        }
                    else -> messageShow("이메일이 존재하지 않습니다.")
                }
            }
        })
    }
    //로그인 버튼

    private fun getContext(): Context {
        var context:Context = this
        return context
    }

    private fun messageShow(text:String){
        Toast.makeText(this,text,Toast.LENGTH_LONG).show()
    }


    fun memberAddButton(v: View){

    }
}