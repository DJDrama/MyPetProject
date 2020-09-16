package com.mypet.www.ui.main.c_chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.mypet.www.R
import com.mypet.www.model.ChatRoom
import kotlinx.android.synthetic.main.item_chat_room.view.*

interface ChatRoomClickListener {
    fun onChatRoomClicked(email: String)
}

class ChatListRecyclerViewAdapter
constructor(
    private val chatRoomClickListener: ChatRoomClickListener?
) :
    ListAdapter<ChatRoom, ChatListRecyclerViewAdapter.ChatRoomViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<ChatRoom>() {
        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem == newItem
        }
    }

    class ChatRoomViewHolder(
        itemView: View,
        private val chatRoomClickListener: ChatRoomClickListener?
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(chatRoom: ChatRoom) {
            chatRoom.users?.let { list ->
                var otherUserEmail=""
                for (user in list) {
                    if (user != FirebaseAuth.getInstance().currentUser?.email) {
                        otherUserEmail = user
                        itemView.tv_with_who.text = otherUserEmail
                        break
                    }
                }
                itemView.setOnClickListener {
                    chatRoomClickListener?.onChatRoomClicked(otherUserEmail)
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        return ChatRoomViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_room, parent, false),
            chatRoomClickListener
        )
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        val chatRoom = getItem(position)
        holder.bind(chatRoom)
    }
}