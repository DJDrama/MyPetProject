package com.mypet.www.ui.main.b_search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mypet.www.model.Cat

class CatViewModel: ViewModel(){
    private val _cat = MutableLiveData<Cat>()
    val cat :LiveData<Cat>
    get() = _cat

    fun setCat(catItem: Cat){
        _cat.value = catItem
    }
}