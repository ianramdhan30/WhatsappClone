package com.example.whatsappclone.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.whatsappclone.fragments.ChatsFragment
import com.example.whatsappclone.fragments.StatusListFragment
import com.example.whatsappclone.fragments.StatusUpdateFragment

class SectionPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val chatsFragment = ChatsFragment()
    private val statusUpdateFragment =
        StatusUpdateFragment()
    private val statusFragment =
        StatusListFragment()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> statusUpdateFragment
            1 -> chatsFragment
            2 -> statusFragment
            else -> chatsFragment
        }
    }

    override fun getCount(): Int {
        return 3
    }
}