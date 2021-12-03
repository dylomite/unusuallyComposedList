package com.example.unusuallycomposedlist.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    val isLoading = MutableLiveData(false)
    val itemsList = MutableLiveData<List<String>>(mutableListOf())

    fun generateItemsList(){
        viewModelScope.launch {
            isLoading.postValue(true)
            delay(1500)
            itemsList.postValue(
                listOf(
                    "AYY",
                    "LMAO!",
                )
            )
            isLoading.postValue(false)
        }
    }
}