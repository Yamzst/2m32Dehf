package com.testlabx.mashle.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.testlabx.mashle.R
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    var mnList:Array<String> = arrayOf("RljdPIfnp0U","RljdPIfnp0U",
        "RljdPIfnp0U","RljdPIfnp0U","RljdPIfnp0U","RljdPIfnp0U",
        "RljdPIfnp0U","RljdPIfnp0U","RljdPIfnp0U","RljdPIfnp0U")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        setRcyQrs()
    }

    fun initData(){
        //mnList.ad

    }

    fun setRcyQrs(){
        /*rcyMnt.setHasFixedSize(true)
        val layoutManagerx = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcyMnt.layoutManager = layoutManagerx

        adapter = MainAdaptert(this,mnList)
        rcyMnt.adapter = adapter*/

    }
}
