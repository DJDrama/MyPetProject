package com.mypet.www.ui.main.c_chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mypet.www.R
import com.mypet.www.model.ChatRoom
import kotlinx.android.synthetic.main.fragment_my_pets.*


class ChatListFragment: Fragment(R.layout.fragment_chat_list), ChatRoomClickListener {
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var chatListRecyclerViewAdapter: ChatListRecyclerViewAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        val linearLayoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = linearLayoutManager

        chatListRecyclerViewAdapter = ChatListRecyclerViewAdapter(this)
        recyclerView.adapter = chatListRecyclerViewAdapter
    }

    override fun onResume() {
        super.onResume()
        retrieveChatRooms()

    }
    private fun retrieveChatRooms(){
        firebaseFirestore.collection("chat").get()
            .addOnSuccessListener {
                val list = it.toObjects(ChatRoom::class.java)
                val newList = mutableListOf<ChatRoom>()
                for(item in list){
                    item.users?.let{l->
                        if(l.contains(firebaseAuth.currentUser?.email)){
                            newList.add(item)
                        }
                    }
                }
                chatListRecyclerViewAdapter.submitList(newList)
            }
    }

    override fun onChatRoomClicked(email: String) {
        val intent = Intent(requireContext(), ChatRoomActivity::class.java)
        intent.putExtra("emailAddress", email)
        startActivity(intent)
    }
}