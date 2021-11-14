package com.testlabx.mashle.help

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class HelpPagerAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {


    companion object{
        private const val ARG_OBJECT = "object"
    }


    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment {

        return when(position){
            0,1 -> {
                val fragment = HelpFragment()
                fragment.arguments = Bundle().apply {
                    putInt(ARG_OBJECT, position + 1)
                }
                fragment
            }
            2 -> {
                SlcFragment()
            }
            else -> HelpFragment()
        }

        //return fragment
    }



}