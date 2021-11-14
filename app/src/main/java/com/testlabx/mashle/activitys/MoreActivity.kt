package com.testlabx.mashle.activitys

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.messaging.FirebaseMessaging
import com.testlabx.mashle.R
import com.testlabx.mashle.helpers.FirebaseEvent
import com.testlabx.mashle.utils.openBrowser
import com.testlabx.mashle.utils.sharedApp
import com.testlabx.mashle.utils.tintBarsTransparent
import kotlinx.android.synthetic.main.activity_more.*
import java.util.*

class MoreActivity : AppCompatActivity() {

    private var mssging:FirebaseMessaging? = null

    private lateinit var storage: SharedPreferences

    private val DATA_NAME = "Mybdata"

    private val SAVE_NOT_ONE = "Swn1"
    private val SAVE_NOT_TWO = "Swn2"
    private val SAVE_NOT_TREE = "Swn3"
    private val SAVE_NOT_FOUR = "Swn4"
    private val SAVE_NOT_FIVE = "Swn5"
    private val SAVE_NOT_SIX = "Swn6"
    private val SAVE_NOT_SEVEN = "Swn7"
    private val SAVE_NOT_EIGHT = "Swn8"
    private val SAVE_FIRST_VEZ = "MyFirstVz"

    //CAMBIAR PAGES
    private var urlUpdt = "https://ston-app.web.app/"
    private var urlStore = "https://ston-app.web.app/"


    private var urlPgOf = "https://ston-app.web.app/"
    private var urlTerms = "https://ston-app.web.app/terms-of-service"
    private var emailContac = "suportredox@gmail.com"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more)
        storage = getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE)

        tintBarsTransparent()

        mssging = FirebaseMessaging.getInstance()


        val frstVz = storage.getBoolean(SAVE_FIRST_VEZ, false)
        if (!frstVz){
            setFirstVz()
            storage.edit().putBoolean(SAVE_FIRST_VEZ, true).apply()
        }


        nswt1.isChecked = getStateSw(SAVE_NOT_ONE)
        nswt2.isChecked = getStateSw(SAVE_NOT_TWO)
        nswt3.isChecked = getStateSw(SAVE_NOT_TREE)
        nswt4.isChecked = getStateSw(SAVE_NOT_FOUR)
        nswt5.isChecked = getStateSw(SAVE_NOT_FIVE)
        nswt6.isChecked = getStateSw(SAVE_NOT_SIX)
        nswt7.isChecked = getStateSw(SAVE_NOT_SEVEN)
        nswt8.isChecked = getStateSw(SAVE_NOT_EIGHT)


        val update = intent.getBooleanExtra("updt",false)
        if (update){
            urlUpdt = intent.getStringExtra("updtUrl").toString()
            cntUpdt.visibility = View.VISIBLE
        }else{
            cntUpdt.visibility = View.GONE
        }

        urlStore = intent.getStringExtra("urlStorex").toString()



        btnUptCnt.setOnClickListener {
            openBrowser(urlUpdt)
            FirebaseEvent.send("clBtUpMr","day",Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toString())
        }

        //CNT SHARED
        imgShrCnt.setOnClickListener {
            sharedApp(urlStore)
        }

        textShrCnt.setOnClickListener {
            sharedApp(urlStore)
        }

        //NOTify
        nswt1.setOnCheckedChangeListener { compoundButton, b ->
            saveStateSw(b,SAVE_NOT_ONE,"Reggaeton")
        }
        nswt2.setOnCheckedChangeListener { compoundButton, b ->
            saveStateSw(b,SAVE_NOT_TWO,"LatinPop")
        }

        nswt3.setOnCheckedChangeListener { compoundButton, b ->
            saveStateSw(b,SAVE_NOT_TREE,"Pop")
        }
        nswt4.setOnCheckedChangeListener { compoundButton, b ->
            saveStateSw(b,SAVE_NOT_FOUR,"Mod")
        }

        nswt5.setOnCheckedChangeListener { compoundButton, b ->
            saveStateSw(b,SAVE_NOT_FIVE,"Tiktok")
        }
        nswt6.setOnCheckedChangeListener { compoundButton, b ->
            saveStateSw(b,SAVE_NOT_SIX,"Bachata")
        }

        nswt7.setOnCheckedChangeListener { compoundButton, b ->
            saveStateSw(b,SAVE_NOT_SEVEN,"LatinPopClasicos")
        }
        nswt8.setOnCheckedChangeListener { compoundButton, b ->
            saveStateSw(b,SAVE_NOT_EIGHT,"Salsa")
        }





        //CNT LINkS
        itm1LnkCnt.setOnClickListener {
            openBrowser(urlPgOf)
        }
        itm2LnkCnt.setOnClickListener {
            openBrowser(urlStore)
        }
        itm3LnkCnt.setOnClickListener {
            val email = arrayOf(emailContac)
            composeEmail(email,getString(R.string.tl_email))
        }
        itm5LnkCnt.setOnClickListener {
            openBrowser(urlTerms)
        }


    }

    private fun setFirstVz() {
        saveStateSw(true,SAVE_NOT_ONE,"Reggaeton")
        saveStateSw(true,SAVE_NOT_TWO,"LatinPop")
        saveStateSw(true,SAVE_NOT_TREE,"Pop")
        saveStateSw(true,SAVE_NOT_FOUR,"Mod")
        saveStateSw(true,SAVE_NOT_FIVE,"Tiktok")
    }


    private fun getStateSw(id:String): Boolean {
        return storage.getBoolean(id, false)
    }


    private fun saveStateSw(newState:Boolean, id:String, tema:String){
        if (newState){
            storage.edit().putBoolean(id, true).apply()
            mssging?.subscribeToTopic(tema)
        }else{
            storage.edit().putBoolean(id, false).apply()
            mssging?.unsubscribeFromTopic(tema)

        }
    }





    private fun composeEmail(addresses: Array<String>, subject: String) {

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }



}