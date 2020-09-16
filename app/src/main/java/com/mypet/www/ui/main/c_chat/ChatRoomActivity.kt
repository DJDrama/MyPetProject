package com.mypet.www.ui.main.c_chat

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mypet.www.R
import com.mypet.www.model.Chat
import com.mypet.www.util.hideKeyBoard
import kotlinx.android.synthetic.main.activity_chat_room.*
import kotlinx.android.synthetic.main.fragment_my_pets.recyclerView
import java.text.SimpleDateFormat
import java.util.*

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var chatRecyclerViewAdapter: ChatRecyclerViewAdapter
    private var emailAddress: String? = null
    private var documentId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        firebaseFirestore = FirebaseFirestore.getInstance()

        emailAddress = intent.getStringExtra("emailAddress")
        emailAddress?.let {
            title = it + "와의 대화"
        }
        checkPastData()

        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager

        chatRecyclerViewAdapter = ChatRecyclerViewAdapter()
        recyclerView.adapter = chatRecyclerViewAdapter
        recyclerView.scrollToPosition(chatRecyclerViewAdapter.itemCount-1)


        realtimeUpdates()
        initSendButton()

    }

    private fun initSendButton() {
        button_send.setOnClickListener {
            if (edit_text.text.isNotEmpty()) {
                uploadMessage(edit_text.text.toString())

                hideKeyBoard(it)
            }
        }
    }

    private fun uploadMessage(msg: String) {
        val data = hashMapOf(
            "msg" to msg,
            "writer" to FirebaseAuth.getInstance().currentUser?.email,
            "date" to SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.KOREA).format(Date())
        )
        documentId?.let {
            firebaseFirestore.collection("chat")
                .document(it).collection("messages")
                .add(data)
                .addOnSuccessListener {
                    //remove edittext string
                    edit_text.text.clear()
                }
                .addOnFailureListener {
                    Log.e("ChatRoomActivity", "failed : " + it)
                }
        }
    }

    private fun realtimeUpdates() {
        documentId?.let {
            firebaseFirestore.collection("chat")
                .document(it).collection("messages")
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    val msg = value?.toObjects(Chat::class.java)
                    msg?.let { chatList ->
                        if (chatList.isEmpty()) return@let
                        chatRecyclerViewAdapter.appendItem(chatList[0])
                        recyclerView.scrollToPosition(chatRecyclerViewAdapter.itemCount-1)

                    }
                }
        }
    }

    private fun checkPastData() {
        var isNeedToMakeNewRoom = false
        firebaseFirestore.collection("chat")
            .whereEqualTo(
                "users",
                listOf(emailAddress, FirebaseAuth.getInstance().currentUser?.email)
            )
            .get()
            .addOnSuccessListener {
                if (it.documents.isEmpty()) {
                    // saveRoomSettingsToFirestore()
                    isNeedToMakeNewRoom = true
                } else {
                    isNeedToMakeNewRoom=false
                    documentId = it.documents[0].id
                    firebaseFirestore.collection("chat")
                        .document(documentId!!).collection("messages")
                        .orderBy("date", Query.Direction.ASCENDING)
                        .get()
                        .addOnSuccessListener { qs ->
                            val chats = qs.toObjects(Chat::class.java)
                            chatRecyclerViewAdapter.submitList(chats)

                        }

                    realtimeUpdates()
                }
            }

        firebaseFirestore.collection("chat")
            .whereEqualTo(
                "users",
                listOf(FirebaseAuth.getInstance().currentUser?.email, emailAddress )
            )
            .get()
            .addOnSuccessListener {
                if (it.documents.isEmpty()) {
                    // saveRoomSettingsToFirestore()
                    isNeedToMakeNewRoom = true
                } else {
                    isNeedToMakeNewRoom=false
                    documentId = it.documents[0].id
                    firebaseFirestore.collection("chat")
                        .document(documentId!!).collection("messages")
                        .orderBy("date", Query.Direction.ASCENDING)
                        .get()
                        .addOnSuccessListener { qs ->
                            val chats = qs.toObjects(Chat::class.java)
                            chatRecyclerViewAdapter.submitList(chats)
                        }

                    realtimeUpdates()
                }
            }

        if (isNeedToMakeNewRoom) {
            saveRoomSettingsToFirestore()
            isNeedToMakeNewRoom = false
        }
    }

    private fun saveRoomSettingsToFirestore() {
        val data = hashMapOf(
            "users" to listOf(emailAddress, FirebaseAuth.getInstance().currentUser?.email)
        )
        firebaseFirestore.collection("chat")
            .add(data)
            .addOnSuccessListener {
                //SUCCESS
                documentId = it.id
                realtimeUpdates()
            }
            .addOnFailureListener {
                //ERROR
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                finish()
        }
        return super.onOptionsItemSelected(item)
    }
}