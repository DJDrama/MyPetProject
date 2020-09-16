package com.mypet.www.ui.main.d_my.recyclerview_adapter

import com.mypet.www.model.Cat

interface CatItemClickListener {
    fun onCatItemClicked(cat: Cat)
    fun onChatClicked(email: String)
}