package com.taxiappclone.common.activity

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.taxiappclone.common.R

class WebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var toolbar: Toolbar? = null
    private var actionBar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        actionBar = supportActionBar
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setHomeButtonEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.title =  intent.getStringExtra("title")

        webView = findViewById(R.id.webView)

        initWebView()

        val htmlContent = intent.getStringExtra("htmlContent")
        webView.loadData(htmlContent, "text/html; charset=utf-8", "UTF-8");
    }

    private fun initWebView() {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        webView.clearCache(true)
        webView.clearHistory()
        webView.settings.javaScriptEnabled = true
        webView.isHorizontalScrollBarEnabled = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}