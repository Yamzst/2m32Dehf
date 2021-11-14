package com.testlabx.mashle.activitys

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayoutMediator
import com.testlabx.mashle.App
import com.testlabx.mashle.R
import com.testlabx.mashle.help.HelpPagerAdapter
import com.testlabx.mashle.helpers.Constants
import com.testlabx.mashle.utils.Varss
import kotlinx.android.synthetic.main.activity_help.*
import kotlinx.android.synthetic.main.fragment_slc.*

class HelpActivity : AppCompatActivity() {

    private lateinit var storage: SharedPreferences
    private val DATOS_NAME = "Mydtb"
    private val SV_AGE = "SvAge"
    private val SV_GEN = "SvGen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        Varss.inHelp = true

        storage = getSharedPreferences(DATOS_NAME, Context.MODE_PRIVATE)


        storage.edit().putInt(Constants.SV_FRZ_CNT, 2).apply()


        val pagerAdapter = HelpPagerAdapter(this)
        helpPager.adapter = pagerAdapter
        TabLayoutMediator(helpTab, helpPager) { tab, position -> }.attach()


        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )


    }


    fun ready(){
        Varss.inHelp = false
        App.getMainActivity()?.simpleExoPlayer?.play()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Varss.inHelp = false
    }


    fun svConfig(age:Int,gen:Int){
        storage.edit().putInt(SV_AGE,age).apply()
        storage.edit().putInt(SV_GEN,gen).apply()
    }




}