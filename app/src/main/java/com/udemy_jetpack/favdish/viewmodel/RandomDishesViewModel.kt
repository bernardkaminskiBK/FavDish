package com.udemy_jetpack.favdish.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RandomDishesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Random dishes Fragment"
    }
    val text: LiveData<String> = _text
}