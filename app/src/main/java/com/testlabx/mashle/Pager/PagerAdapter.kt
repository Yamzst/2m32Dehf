package com.testlabx.mashle.Pager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.testlabx.mashle.App
import com.testlabx.mashle.fragments.ShowFragment

class PagerAdapter(fa:FragmentActivity):FragmentStateAdapter(fa) {
    val hashMap: HashMap<Int, Fragment> = HashMap()
    override fun getItemCount(): Int = App.getMainActivity()?.simpleExoPlayer!!.mediaItemCount///Varss.idsN//Varss.urlLs.size

    override fun createFragment(position: Int): Fragment {
        val fragment = ShowFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt("pgx", position + 1)
        }
        hashMap[position] = fragment
        return fragment
        //return ShowFragment()
    }
}