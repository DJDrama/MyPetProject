package com.mypet.www.ui.main.d_my

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mypet.www.R
import com.mypet.www.model.Cat
import com.mypet.www.ui.launcher.LauncherActivity
import com.mypet.www.ui.main.AddPetActivity
import kotlinx.android.synthetic.main.fragment_my_pets.*


class MyCatsFragment : Fragment(R.layout.fragment_my_pets), CatItemClickListener {
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var catRecyclerViewAdapter: CatRecyclerViewAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        catRecyclerViewAdapter = CatRecyclerViewAdapter(this)
        recyclerView.adapter = catRecyclerViewAdapter
    }

    override fun onResume() {
        super.onResume()
        retrieveMyCats()

    }

    private fun retrieveMyCats() {
        firebaseFirestore.collection("cats")
            .whereEqualTo("uid", firebaseAuth.currentUser?.uid)
            .get()
            .addOnSuccessListener {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_post, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_post -> {
                navToAddPostActivity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navToAddPostActivity() {
        startActivity(Intent(requireContext(), AddPetActivity::class.java))
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
            firebaseFirestore.collection("cats").document(cat.documentId).delete()
                .addOnSuccessListener {
                    //SUCCESS
                    retrieveMyCats()
                }
                .addOnFailureListener {
                    //ERROR
                }
        }
        builder.setNegativeButton(
            "취소"
        ) { d, _ ->
            d.dismiss()
        }
        builder.show()
    }
}