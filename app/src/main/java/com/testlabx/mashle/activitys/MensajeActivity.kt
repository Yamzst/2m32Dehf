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


}