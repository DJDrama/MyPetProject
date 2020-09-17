package com.mypet.www.ui.main.b_search

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mypet.www.R
import com.mypet.www.model.Cat
import com.mypet.www.model.Like
import com.mypet.www.ui.main.c_chat.ChatRoomActivity
import com.mypet.www.ui.main.d_my.recyclerview_adapter.CatItemClickListener
import com.mypet.www.ui.main.d_my.recyclerview_adapter.CatRecyclerViewAdapter
import com.mypet.www.util.showToast
import kotlinx.android.synthetic.main.fragment_my_pets.*


class SearchFragment: Fragment(R.layout.fragment_search), CatItemClickListener {
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var catRecyclerViewAdapter: CatRecyclerViewAdapter
    private val catViewModel: CatViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        catRecyclerViewAdapter = CatRecyclerViewAdapter(this)
        recyclerView.adapter = catRecyclerViewAdapter

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_reload, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_filter -> {
                navigateToFilterFragment()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun navigateToFilterFragment(){

    }
    override fun onResume() {
        super.onResume()
        retrieveCats()
    }
    private fun retrieveCats() {
        firebaseFirestore.collection("cats")
            .get()
            .addOnSuccessListener {
                firebaseFirestore.collection("cats")
                    .get()
                    .addOnSuccessListener {qs->
                        val list = mutableListOf<Cat>()
                        for(document in it.documents){
                            val cat = document.toObject(Cat::class.java)
                            cat?.documentId = document.id
                            cat?.let{catItem->
                                list.add(catItem)
                            }
                        }
                        if(list.size!=0){
                            catRecyclerViewAdapter.submitList(list)
                        }
                    }
                    .addOnFailureListener {

                    }
            }

    }
    override fun onCatItemClicked(cat: Cat) {
        catViewModel.setCat(cat)

        findNavController().navigate(R.id.action_searchFragment_to_catDetailFragment)
    }

    override fun onChatClicked(email: String) {
        if(email == firebaseAuth.currentUser?.email){
            requireContext().showToast("자신과는 대화할 수 없습니다.")
            return
        }
        val intent = Intent(requireContext(), ChatRoomActivity::class.java)
        intent.putExtra("emailAddress", email)
        startActivity(intent)
    }
}