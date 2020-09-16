package com.mypet.www.ui.main.d_my.recyclerview_adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.mypet.www.model.Cat
import com.mypet.www.util.GlideApp
import kotlinx.android.synthetic.main.item_cat.view.*

class CatViewHolder(
    itemView: View,
    private val catItemClickListener: CatItemClickListener?
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(cat: Cat) {
            with(itemView) {
                if(cat.uid == FirebaseAuth.getInstance().currentUser?.uid){
                    iv_chat.visibility=View.GONE
                }

                val ref = FirebaseStorage.getInstance().reference
                val pathReference = ref.child("images/"+cat.image)
                GlideApp.with(this.context).load(pathReference).into(imageView5)
                tv_name.text = cat.name
                tv_age.text = cat.age
                tv_gender.text = cat.gender
                tv_kind.text = cat.kind
                tv_owner.text = cat.email
                setOnClickListener {
                    catItemClickListener?.onCatItemClicked(cat)
                }
                iv_chat.setOnClickListener {
                    catItemClickListener?.onChatClicked(cat.email)
                }
            }
        }
    }