package com.testlabx.mashle.activitys


import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.testlabx.mashle.R
import com.testlabx.mashle.utils.toast
import kotlinx.android.synthetic.main.activity_more.*
import kotlinx.android.synthetic.main.activity_web_ad.*

class WebAdActivity : AppCompatActivity() {


    val TAG = ""

    var initTimer = false

    var adCompleto = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_ad)


        val urlAd = intent.getStringExtra("updt")



        adWebView.webChromeClient = object : WebChromeClient(){

        }
        adWebView.webViewClient = object : WebViewClient(){
            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
                Log.i(TAG,"doUpdate $url")
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                if (request != null) {
                    Log.i(TAG,"shouldOver ${request.url}")
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onLoadResource(view: WebView?, url: String?) {
                super.onLoadResource(view, url)
                Log.i(TAG,"onLoad $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.i(TAG,"onPageFinish $url")
                //solo una vez
                if (!initTimer){
                    initTimer = true
                    timerAd()
                }

            }
        }

        val settings = adWebView.settings
        //settings.allowContentAccess = true
        //settings.databaseEnabled = true
        settings.javaScriptEnabled = true


        //"https://filtrosinstagram.blogspot.com/2021/11/testads.html"

        adWebView.loadUrl(urlAd!!)


        txCountAds.setOnClickListener {
            finish()
        }

        //adwebview
    }


    fun timerAd(){
        /*Handler(Looper.getMainLooper()).postDelayed({
            //


        }, 5000)*/
        val timer = object: CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                //change text
                txCountAds.text = millisUntilFinished.toString()
            }

            override fun onFinish() {
                adCompleto = true
                Toast.makeText(this@WebAdActivity, "finishAd", Toast.LENGTH_SHORT).show()
            //show buton

            }
        }
        timer.start()

    }


    override fun onBackPressed() {
        if (adCompleto){
            super.onBackPressed()
        }
    }



}