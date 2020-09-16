package com.mypet.www.ui.main.d_my

import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.with
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.with
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.with
import com.google.firebase.storage.FirebaseStorage
import com.mypet.www.R
import com.mypet.www.model.Cat
import com.mypet.www.util.AppGlide
import com.mypet.www.util.GlideApp
import kotlinx.android.synthetic.main.item_cat.view.*

interface CatItemClickListener {
    fun onCatItemClicked(cat: Cat)
}

class CatRecyclerViewAdapter
constructor(
    private val catItemClickListener: CatItemClickListener?
) :
    ListAdapter<Cat, CatRecyclerViewAdapter.CatViewHolder>(DiffCallback) {

    class CatViewHolder(
        itemView: View,
        private val catItemClickListener: CatItemClickListener?
    ) : RecyclerView.ViewHolder(itemView) {
        fun bind(cat: Cat) {
            with(itemView) {
                val ref = FirebaseStorage.getInstance().reference
                val pathReference = ref.child("images/"+cat.image)
                GlideApp.with(this.context).load(pathReference).into(imageView5)
                tv_name.text = cat.name
                tv_age.text = cat.age
                tv_gender.text = cat.gender
                tv_kind.text = cat.kind
                setOnClickListener {
                    catItemClickListener?.onCatItemClicked(cat)
                }
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Cat>() {
        override fun areItemsTheSame(oldItem: Cat, newItem: Cat): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Cat, newItem: Cat): Boolean {
            return oldItem.name == newItem.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
        return CatViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_cat, parent, false),
            catItemClickListener
        )
    }

    override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
        val cat = getItem(position)
        holder.bind(cat)
    }
}