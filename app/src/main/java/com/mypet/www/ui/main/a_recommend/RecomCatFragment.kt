package com.mypet.www.ui.main.a_recommend

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mypet.www.R
import com.mypet.www.model.Cat
import com.mypet.www.model.Like
import com.mypet.www.ui.main.c_chat.ChatRoomActivity
import com.mypet.www.util.GlideApp
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.fragment_today_pet.*
import kotlinx.android.synthetic.main.fragment_today_pet.tv_gender
import kotlinx.android.synthetic.main.fragment_today_pet.view.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class RecomCatFragment : Fragment(R.layout.fragment_today_pet) {
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        retrieveData()




    }

    private fun retrieveData() {
        firebaseFirestore.collection("cats")
            .get().addOnSuccessListener {
                val list = mutableListOf<Cat>()
                for(document in it.documents){
                    val cat = document.toObject(Cat::class.java)
                    cat?.documentId = document.id
                    cat?.let{catItem->
                        list.add(catItem)
                    }
                }
                var randomNumber: Int
                lifecycleScope.launch(Default) {
                    while (true) {
                        randomNumber = Random.nextInt(0, list.size)
                        val uid = list[randomNumber].uid
                        if (uid == firebaseAuth.currentUser?.uid) {
                            continue
                        } else {
                            break
                        }
                    }
                    withContext(Main) {
                        setCatItem(list[randomNumber])
                    }
                }
            }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setCatItem(cat: Cat?) {
        cat?.let {c->
            val ref = FirebaseStorage.getInstance().reference
            val pathReference = ref.child("images/" + c.image)
            GlideApp.with(requireContext()).load(pathReference).into(imageView6)
            tv_name.text = c.name
            tv_age.text = c.age
            tv_gender.text = c.gender
            tv_kind.text = c.kind
            tv_owner.text = c.email

            iv_like.visibility = View.VISIBLE

            //CHECK If LIKED
            firebaseFirestore.collection("likes").whereEqualTo("uid", firebaseAuth.currentUser?.uid)
                .whereEqualTo("documentId", c.documentId)
                .get()
                .addOnSuccessListener { qs ->
                    val likes = qs.toObjects(Like::class.java)
                    if (likes.isNotEmpty()) {
                        //Liked already
                        iv_like.setImageResource(R.drawable.ic_baseline_favorite)
                        iv_like.tag = "liked"
                    }else{
                        iv_like.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                        iv_like.tag = "unlike"
                    }
                }

            iv_like.setOnClickListener { view ->
                //already liked
                if (iv_like.tag == "liked") {
                    firebaseFirestore.collection("likes")
                        .whereEqualTo("uid", firebaseAuth.currentUser?.uid)
                        .whereEqualTo("documentId", c.documentId)
                        .get()
                        .addOnSuccessListener {qs->
                            qs.documents[0].reference.delete().addOnSuccessListener {
                                iv_like.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                                iv_like.tag = "unlike"
                                Toast.makeText(requireContext(), "좋아요를 해제 하였습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    val documentId = c.documentId
                    val uid = firebaseAuth.currentUser?.uid
                    val email = firebaseAuth.currentUser?.email

                    val data = hashMapOf(
                        "documentId" to documentId,
                        "email" to email,
                        "uid" to uid
                    )
                    firebaseFirestore.collection("likes")
                        .add(data)
                        .addOnSuccessListener {
                            //SUCCESS
                            Toast.makeText(requireContext(), "좋아요를 눌렀습니다.", Toast.LENGTH_SHORT)
                                .show()
                            view.iv_like.setImageResource(R.drawable.ic_baseline_favorite)
                            iv_like.tag = "liked"
                        }
                        .addOnFailureListener {
                            //ERROR
                            Toast.makeText(
                                requireContext(),
                                "에러 발생! 다시 시도해주세요.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }


            }

            iv_chat.setOnClickListener{
                val intent =Intent(requireContext(), ChatRoomActivity::class.java)
                intent.putExtra("emailAddress", c.email)
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_reload, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_reload -> {
                reloadItem()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun reloadItem() {
        retrieveData()
    }

}