package com.mypet.www.ui.main.d_my

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mypet.www.R
import com.mypet.www.model.Cat
import com.mypet.www.model.Like
import com.mypet.www.ui.main.c_chat.ChatRoomActivity
import com.mypet.www.ui.main.d_my.recyclerview_adapter.CatItemClickListener
import com.mypet.www.ui.main.d_my.recyclerview_adapter.CatRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_my_pets.*

class MyLikesFragment: Fragment(R.layout.fragment_my_likes), CatItemClickListener {
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var catRecyclerViewAdapter: CatRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        catRecyclerViewAdapter = CatRecyclerViewAdapter(this)
        recyclerView.adapter = catRecyclerViewAdapter
    }
    override fun onResume() {
        super.onResume()
        retrieveMyLikes()
    }
    private fun retrieveMyLikes() {
        firebaseFirestore.collection("likes")
            .whereEqualTo("uid", firebaseAuth.currentUser?.uid)
            .get()
            .addOnSuccessListener {
                //getting my likes from likes collection
                val likes = it.toObjects(Like::class.java)
                firebaseFirestore.collection("cats")
                    .get()
                    .addOnSuccessListener {qs->
                        val list = mutableListOf<Cat>()
                        for(document in qs.documents){
                            val cat = document.toObject(Cat::class.java)
                            cat?.documentId = document.id
                            cat?.let{catItem->
                                list.add(catItem)
                            }
                        }
                        val likesList = mutableListOf<Cat>()
                        for(like in likes){
                            for(cat in list){
                                if(like.documentId == cat.documentId){
                                    likesList.add(cat)
                                }
                            }
                        }
                        if(list.size!=0){
                            catRecyclerViewAdapter.submitList(likesList)
                        }
                    }
                    .addOnFailureListener {

                    }
            }

    }
    override fun onCatItemClicked(cat: Cat) {
        // DELETE
        val builder: AlertDialog.Builder = AlertDialog.Builder(
            this.requireContext()
        )
        builder.setTitle("삭제")
        builder.setMessage("정말 삭제 하시겠습니까?")
        builder.setPositiveButton(
            "확인"
        ) { d, _ ->
            firebaseFirestore.collection("likes").whereEqualTo("uid",firebaseAuth.currentUser?.uid)
                .whereEqualTo("documentId", cat.documentId)
                .get()
                .addOnSuccessListener {
                    val likeDocumentId = it.documents[0].id
                    firebaseFirestore.collection("likes").document(likeDocumentId).delete()
                        .addOnSuccessListener {
                            //SUCCESS
                            retrieveMyLikes()
                        }
                        .addOnFailureListener {
                            //ERROR
                        }
                }


        }
        builder.setNegativeButton(
            "취소"
        ) { d, _ ->
            d.dismiss()
        }
        builder.show()
    }

    override fun onChatClicked(email: String) {
        val intent = Intent(requireContext(), ChatRoomActivity::class.java)
        intent.putExtra("emailAddress", email)
        startActivity(intent)
    }
}