package com.testlabx.mashle.help

import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import com.testlabx.mashle.R
import com.testlabx.mashle.activitys.HelpActivity
import kotlinx.android.synthetic.main.fragment_pager_help.*
import kotlinx.android.synthetic.main.fragment_slc.*
import java.util.*


class SlcFragment : Fragment(R.layout.fragment_slc) {

    private var age = 0
    private var gen  = 3

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var values = arrayOf("Hombre", "Mujer", "Personalizado")

        if (Locale.getDefault().language != "es" && Locale.getDefault().toString() != "pt_BR") {
            tlAge.text = "Age"
            tlGen.text = "Gender"
            values = arrayOf("Male", "Female", "Custom")
        }

        slcAge.minValue = 0
        slcAge.maxValue = 100
        slcAge.value = 22
        slcAge.wrapSelectorWheel = true
        slcAge.setOnValueChangedListener { picker, oldVal, newVal ->
            if (age in 19..49){
                age = newVal
            }
        }


        slcGen.minValue = 0
        slcGen.maxValue = values.size - 1
        slcGen.displayedValues = values
        slcGen.wrapSelectorWheel = true
        slcGen.setOnValueChangedListener { picker, oldVal, newVal ->
            if (values[newVal] != "Personalizado" && values[newVal] != "Custom"){
                gen = newVal
            }

        }



        btnStart.setOnClickListener {
            (activity as HelpActivity).svConfig(age,gen)
            myHandler.removeCallbacksAndMessages(null)
            (activity as HelpActivity).ready()
        }

        loopAnimPlay()



    }


    var tpAnim = 1
    var myHandler = Handler(Looper.getMainLooper())
    private fun loopAnimPlay() {
        myHandler.post(object : Runnable {
            override fun run() {

                if (tpAnim == 1) {

                    btnStart.setImageResource(R.drawable.anim_play_pause)
                    (btnStart.drawable as? Animatable)?.start()
                    tpAnim = 2

                } else {

                    btnStart.setImageResource(R.drawable.anim_pause_play)
                    (btnStart.drawable as? Animatable)?.start()
                    tpAnim = 1

                }
                myHandler.postDelayed(this, 1200)
            }
        })
    }


    override fun onStop() {
        (activity as HelpActivity).svConfig(age,gen)
        myHandler.removeCallbacksAndMessages(null)
        super.onStop()
    }




}