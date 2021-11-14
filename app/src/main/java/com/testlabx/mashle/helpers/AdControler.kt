package com.testlabx.mashle.helpers

import android.widget.Toast
import com.testlabx.mashle.App
import com.testlabx.mashle.utils.Varss


object AdControler {

    //add showAd a buscar querry main y list y clixk querri
    fun showAdItrs(){
        if (Varss.pdIrts){
            //Toast.makeText(activity, "showAd", Toast.LENGTH_SHORT).show()
            App.getMainActivity()?.stopTmrItrs()
            Varss.pdIrts = false
            App.getMainActivity()?.initItrs(FirebaseRC.dsIrt)
            Varss.dwCount = 0
            App.getMainActivity()?.showIrts()

        }
    }
}