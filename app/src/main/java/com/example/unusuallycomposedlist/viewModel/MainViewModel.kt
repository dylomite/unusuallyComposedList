package com.example.unusuallycomposedlist.viewModel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.unusuallycomposedlist.model.ImageItemModel
import kotlinx.coroutines.launch
import java.io.IOException

class MainViewModel(app: Application) : AndroidViewModel(app) {

    val isLoading = MutableLiveData(false)
    val itemsList = MutableLiveData<List<ImageItemModel>>(mutableListOf())

    private fun getBitmapFromAsset(context: Context, assetName: String) = try {
        val assetInputStream = context.assets.open(assetName)
        BitmapFactory.decodeStream(assetInputStream)
    } catch (e: IOException) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    fun generateItemsList(context: Context) {
        viewModelScope.launch {
            isLoading.postValue(true)
            itemsList.postValue(
                listOf(
                    ImageItemModel("SOME", getBitmapFromAsset(context, "auronzo.jpeg")),
                    ImageItemModel("BEAUTIFUL", getBitmapFromAsset(context, "dobiacco.jpeg")),
                    ImageItemModel("LANDSCAPES", getBitmapFromAsset(context, "pasubio.jpeg")),
                    ImageItemModel("MOO!", getBitmapFromAsset(context, "siusi.jpeg")),
                )
            )
            isLoading.postValue(false)
        }
    }
}