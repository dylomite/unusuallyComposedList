package com.example.unusuallycomposedlist.viewModel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.unusuallycomposedlist.model.ImageItemModel
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {

    val isLoading = MutableLiveData(false)
    val itemsList = MutableLiveData<List<ImageItemModel>>(mutableListOf())

    fun generateItemsList(){
        viewModelScope.launch {
            isLoading.postValue(true)
            itemsList.postValue(
                listOf(
                    ImageItemModel("", Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888))
                )
            )
            isLoading.postValue(false)
        }
    }
}