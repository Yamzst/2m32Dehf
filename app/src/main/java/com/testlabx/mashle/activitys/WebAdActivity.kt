package com.testlabx.mashle.activitys


import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.testlabx.mashle.App
import com.testlabx.mashle.R
import com.testlabx.mashle.helpers.FirebaseRC
import com.testlabx.mashle.utils.*
import kotlinx.android.synthetic.main.activity_more.*
import kotlinx.android.synthetic.main.activity_web_ad.*
import android.content.Intent
import android.net.Uri
import android.os.*
import android.webkit.*
import android.webkit.WebView.HitTestResult
import java.lang.Exception


class WebAdActivity : AppCompatActivity() {


    val TAG = "tsAdsWebx"

    var seconds = 5

    var initTimer = false
    var adCompleto = false

    var tpPg = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_ad)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true
        tintBarSt(getColorx(R.color.white))
        tintBarNv(getColorx(R.color.white))

        seconds = FirebaseRC.tmAd

        btnClsAdWeb.visibility = View.GONE
        txCountAds.visibility = View.GONE
        ctnMsjAd.visibility = View.GONE
        ctnMsjAd.setOnClickListener { }

        var urlAd = intent.getStringExtra("pgAd")

        if (urlAd!!.contains("adf:")){
            urlAd = urlAd.replace("adf:","")
            ctnMsjAd.visibility = View.VISIBLE
            tpPg = "adfly"
        }



        adWebView.webChromeClient = object : WebChromeClient(){

            override fun onCreateWindow(view: WebView, dialog: Boolean, userGesture: Boolean, resultMsg: Message?
            ): Boolean {
                Log.i(TAG,"oncreate new windows")
                return false
            }

            override fun onJsAlert(view: WebView?, url: String?, message: String?, result: JsResult?
            ): Boolean {
                Log.i(TAG,"onJsAlert")
                result?.cancel()
                return true
                //return super.onJsAlert(view, url, message, result)
            }

            override fun onJsConfirm(view: WebView?, url: String?, message: String?, result: JsResult?
            ): Boolean {
                Log.i(TAG,"onJsConfirm")
                result?.cancel()
                return true
                //return super.onJsConfirm(view, url, message, result)
            }


            override fun onJsPrompt(view: WebView?, url: String?, message: String?, defaultValue: String?, result: JsPromptResult?
            ): Boolean {
                Log.i(TAG,"onJsPrompt")
                result?.cancel()
                return true
                //return super.onJsPrompt(view, url, message, defaultValue, result)
            }




        }


        var urlLoads = 0
        adWebView.webViewClient = object : WebViewClient(){
            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)

                if (tpPg == "adfly") {
                    urlLoads += 1
                    if (urlLoads == 2) {

                        msjAd.text = "Anuncio"

                        if (!url!!.contains("https://adf.ly") ){

                            Handler(Looper.getMainLooper()).postDelayed({
                                adCompleto = true
                                //btnClsAdWeb.visibility = View.VISIBLE
                                //ctnMsjAd.visibility = View.GONE
                                finish()
                            }, 2000)

                        }else{
                            Handler(Looper.getMainLooper()).postDelayed({
                                adCompleto = true
                                btnClsAdWeb.visibility = View.VISIBLE
                            }, 6000)

                        }
                    }

                    Log.i(TAG, "doUpdate $url")
                }else{

                    msjAd.text = "Publicidad"
                    ctnMsjAd.visibility = View.VISIBLE
                    urlLoads += 1
                    if (urlLoads == 2) {
                        Handler(Looper.getMainLooper()).postDelayed({
                            finish()
                        }, 5000)
                    }
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                if (request?.url.toString().contains("intent://")){
                    return true
                }

                return super.shouldOverrideUrlLoading(view, request)
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.i(TAG,"onPageFinish $url")

                if (tpPg == "") {
                    if (!initTimer) {
                        initTimer = true

                        timerAd()
                    }
                }

            }
        }


        val settings = adWebView.settings
        //settings.allowContentAccess = true
        //settings.databaseEnabled = true
        //settings.userAgentString = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1"
        settings.javaScriptEnabled = true

        adWebView.loadUrl(urlAd)


        btnClsAdWeb.setOnClickListener {
            finish()
        }

        btnPsAdWeb.setOnClickListener {
            App.getMainActivity()?.simpleExoPlayer?.playWhenReady = false
            it.visibility = View.GONE
        }

    }


    fun timerAd(){
        txCountAds.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            seconds -= 1
            txCountAds.text = seconds.toString()
            if (seconds == 0){
                adCompleto = true
                btnClsAdWeb.visibility = View.VISIBLE
                txCountAds.visibility = View.GONE
            }else{
                timerAd()
            }
        }, 1000)


    }


    override fun onBackPressed() {
        if (adCompleto){
            super.onBackPressed()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        adWebView.loadUrl("")
    }






}