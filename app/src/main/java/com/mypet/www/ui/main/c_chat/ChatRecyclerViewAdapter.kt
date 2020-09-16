package com.mypet.www.ui.main.c_chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mypet.www.R
import com.mypet.www.model.Cat
import com.mypet.www.model.Chat
import com.mypet.www.ui.main.d_my.recyclerview_adapter.CatViewHolder
import kotlinx.android.synthetic.main.item_chat_receive.view.*
import kotlinx.android.synthetic.main.item_chat_send.view.*

class ChatRecyclerViewAdapter : RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatViewHolder>() {
    companion object {
        const val SEND = 4242
        const val RECEIVE = 2424
    }

    private val chatList: MutableList<Chat> = mutableListOf()

    fun submitList(cl: List<Chat>) {
        chatList.clear()
        chatList.addAll(cl)
        notifyDataSetChanged()
    }

    fun appendItem(chat: Chat) {
        chatList.add(chat)

        notifyDataSetChanged()
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chat: Chat) {
            if (chat.writer == FirebaseAuth.getInstance().currentUser?.email) {
                itemView.tv_send.text = chat.msg
            } else {
                itemView.tv_receive.text = chat.msg
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        when (viewType) {
            SEND -> {
                return ChatViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_chat_send, parent, false)
                )
            }
            RECEIVE -> {
                return ChatViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_chat_receive, parent, false)
                )
            }
            else -> {
                return ChatViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_chat_send, parent, false)
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemViewType(position: Int): Int {
        val chatItem = chatList[position]
                (chatItem.writer==FirebaseAuth.getInstance().currentUser?.email))
        return if (chatItem.writer == FirebaseAuth.getInstance().currentUser?.email) {
            SEND
        } else {
            RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

}