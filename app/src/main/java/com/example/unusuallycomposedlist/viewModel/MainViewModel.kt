package com.example.unusuallycomposedlist.viewModel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    val isLoading = MutableLiveData(false)
    val itemsList = MutableLiveData<List<String>>(mutableListOf())
    val scrollOffsetPerc = MutableLiveData(0)

    fun generateItemsList(){
        viewModelScope.launch {
            isLoading.postValue(true)
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