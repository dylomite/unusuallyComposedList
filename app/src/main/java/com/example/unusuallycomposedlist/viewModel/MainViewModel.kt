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

    fun generateItemsList(){
        viewModelScope.launch {
            isLoading.postValue(true)
            itemsList.postValue(
                listOf(
                    "AYY",
                    "LMAO!",
                    "LMAO1",
                    "LMAO2",
                    "LMAO3",
                )
            )
            isLoading.postValue(false)
        }
    }
}