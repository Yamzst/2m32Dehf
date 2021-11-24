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
import com.testlabx.mashle.helpers.Constants
import com.testlabx.mashle.helpers.FirebaseEvent
import com.testlabx.mashle.utils.tintBarsTransparent
import com.testlabx.mashle.utils.tintUi
import kotlinx.android.synthetic.main.activity_update.*
import java.util.*

class UpdateActivity : AppCompatActivity() {

    private var updtFor =  false
    var updtUrl = Constants.UPD_URL

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


        if (intent.getStringExtra("updtUrl").toString() != ""){
            updtUrl = intent.getStringExtra("updtUrl").toString()
        }



        updt_btn.setOnClickListener {
            if (updtUrl.contains("https://")){
                val intentNavegador = Intent(Intent.ACTION_VIEW, Uri.parse(updtUrl))
                startActivity(intentNavegador)
                FirebaseEvent.send("clkBtnUpdt","day", dayOfWeek().toString())
            }
        }



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