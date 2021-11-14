package com.testlabx.mashle.activitys

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.testlabx.mashle.R
import com.testlabx.mashle.utils.tintBarsTransparent
import com.testlabx.mashle.utils.tintUi
import kotlinx.android.synthetic.main.activity_update.*
import java.util.*

class UpdateActivity : AppCompatActivity() {
    private var updtFor =  false

    private val anlts = FirebaseAnalytics.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        tintBarsTransparent()

        val updtTp = intent.getStringExtra("updtTp")
        if (updtTp != null) {
            updtFor = if (updtTp == "A"){
                false
            }else{
                true
            }
        }


        val updtUrl = intent.getStringExtra("updtUrl")

        updt_btn.setOnClickListener {
            if (updtUrl != "" && updtUrl!!.contains("https://")){
                val intentNavegador = Intent(Intent.ACTION_VIEW, Uri.parse(updtUrl))
                startActivity(intentNavegador)
                sendEvent("clkBtnUpdt","day", dayOfWeek().toString())
            }
        }



    }


    fun sendEvent(nmE:String,key:String,value:String){
        val bundle = Bundle()
        bundle.putString(key, value)
        anlts.logEvent(nmE, bundle)
    }

    private fun dayOfWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    }

    override fun onBackPressed() {
        if (!updtFor){
            super.onBackPressed()
        }else{
            val intentNavegador = Intent(Intent.ACTION_MAIN)
            intentNavegador.addCategory(Intent.CATEGORY_HOME)
            //startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentNavegador)
        }
    }




}