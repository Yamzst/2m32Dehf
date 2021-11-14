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
import java.util.*


class HelpFragment : Fragment(R.layout.fragment_pager_help) {

    companion object{
        private const val ARG_OBJECT = "object"
    }
    private var qfrag = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
            qfrag = getInt(ARG_OBJECT)
        }


        setLnj(qfrag)

    }

    private fun setLnj(fg:Int){

        when(fg){
            1 ->{
                tlPgrHelp.text = getString(R.string.help_f1_tl)
                txPgrHelp.text = getString(R.string.help_f1_ctn)
            }
            2 ->{
                tlPgrHelp.text = getString(R.string.help_f2_tl)
                txPgrHelp.text = getString(R.string.help_f2_ctn)
                imgPgrHelp.setImageResource(R.drawable.ic_music)
            }
        }


    }




}