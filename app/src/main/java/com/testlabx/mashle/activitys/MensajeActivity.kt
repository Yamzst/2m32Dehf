package com.testlabx.mashle.activitys

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.testlabx.mashle.R
import com.testlabx.mashle.utils.tintBarsTransparent
import kotlinx.android.synthetic.main.activity_mensaje.*
import java.util.*

class MensajeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mensaje)

        tintBarsTransparent()

        val msj = intent.getStringExtra("msjx")
        if (msj != null) {
            msj_cntn.text = msj
        }

        val urlMsj = intent.getStringExtra("urlMsjx").toString()
        if (urlMsj == ""){
            msj_btn.visibility = View.GONE
        }

        msj_btn.setOnClickListener {
            if (urlMsj.contains("https://")){
                val intentNavegador = Intent(Intent.ACTION_VIEW, Uri.parse(urlMsj))
                startActivity(intentNavegador)
            }
        }

    }

    fun uiMode():Boolean{
        return when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED ->false
            else -> false
        }
    }

    fun tintUi(){
        if (!uiMode()){
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = true
            window.apply {
                //statusBarColor = ContextCompat.getColor(applicationContext, R.color.tsnm)
                //navigationBarColor = ContextCompat.getColor(applicationContext, R.color.tssc)
            }
        }else{
            window.apply {
                statusBarColor = ContextCompat.getColor(applicationContext, R.color.black)
                navigationBarColor = ContextCompat.getColor(applicationContext, R.color.black)
            }
        }
    }


}