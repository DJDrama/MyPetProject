package com.mypet.www.ui.main.c_chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatListViewModel: ViewModel(){
    private val _isNewRoomNeeded: MutableLiveData<Boolean> = MutableLiveData()
    val isNewRoomNeeded: LiveData<Boolean>
    get()=_isNewRoomNeeded

    init{
        _isNewRoomNeeded.value = false
    }

    fun setIsNewRoomNeeded(isNeeded: Boolean){
        _isNewRoomNeeded.value = isNeeded
    }
    fun getIsNewRoomNeeded(): Boolean?{
        return _isNewRoomNeeded.value
    }
}