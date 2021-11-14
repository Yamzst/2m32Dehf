package com.testlabx.mashle.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.testlabx.mashle.R
import com.testlabx.mashle.utils.NetworkConnection
import com.testlabx.mashle.utils.btnHome
import kotlinx.android.synthetic.main.activity_wifi.*
import kotlinx.coroutines.flow.collect

class WifiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)

        NetworkConnection.initialize(this)
        lifecycleScope.launchWhenStarted {
            NetworkConnection.isConnected.collect { isConnected ->
                if (isConnected){
                    //play video no necesario
                    finish()

                }

            }
        }











    }


    override fun onBackPressed() {
        btnHome()
    }


}