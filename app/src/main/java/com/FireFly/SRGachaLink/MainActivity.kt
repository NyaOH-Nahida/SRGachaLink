package com.FireFly.SRGachaLink

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.domStorageEnabled = true
        myWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // 注入 JavaScript 代码来修改 navigator.language
                view?.evaluateJavascript(
                    """
                            Object.defineProperty(navigator, 'language', {get: function(){return 'zh-CN';}});
                            Object.defineProperty(navigator, 'languages', {get: function(){return ['zh-CN'];}});
                          """, null
                )
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                request?.let {
                    if (it.url.toString().contains("api-takumi.mihoyo.com/common/gacha_record/api/getGachaLog")) {
                        //抓取请求头为api-takumi.mihoyo.com的链接
                        //之前因为误抓取公告链接，现已增加相关关键词匹配
                        // 在主线程中执行 UI 操作
                        runOnUiThread {
                            Log.d("WebViewRequest", "URL being loaded: ${it.url}")
                            val editText = findViewById<EditText>(R.id.input)
                            editText.setText(it.url.toString())
                            val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clipData = ClipData.newPlainText("", it.url.toString())
                            cm.setPrimaryClip(clipData)
                            Toast.makeText(
                                this@MainActivity,
                                "已复制到剪贴板，若未复制请手动复制下方链接",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    view?.loadUrl(it)
                }
                return true
            }
        }


        myWebView.loadUrl("https://sr.mihoyo.com/cloud/#/")
    }
}

fun Int.onClick(activity: Activity, click: () -> Unit) {
    activity.findViewById<View>(this).setOnClickListener {
        click()
    }
}

