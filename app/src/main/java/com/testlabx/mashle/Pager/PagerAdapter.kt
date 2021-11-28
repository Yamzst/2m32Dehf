package com.testlabx.mashle.Pager

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.testlabx.mashle.App
import com.testlabx.mashle.fragments.ShowFragment

class PagerAdapter(fa:FragmentActivity):FragmentStateAdapter(fa) {
    val hashMap: HashMap<Int, Fragment> = HashMap()

    override fun getItemCount(): Int = App.getMainActivity()?.simpleExoPlayer!!.mediaItemCount

    override fun createFragment(position: Int): Fragment {
        val fragment = ShowFragment()
        fragment.arguments = Bundle().apply {
            putInt("pgx", position)
        }
        hashMap[position] = fragment
        return fragment
    }
}