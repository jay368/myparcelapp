package com.example.myparcelapp

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_html.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class HtmlActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_html)

        val wb:WebView = WebView(this)
        wb.loadUrl("http://192.168.55.231:8080/sessiontest/")
        webView.loadUrl("http://192.168.55.231:8080/productpage_json?pid=MW==")
    }

}




