package com.example.unusuallycomposedlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.example.unusuallycomposedlist.theme.AppTheme
import com.example.unusuallycomposedlist.theme.primaryLight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                Column(Modifier.fillMaxSize().background(primaryLight)) {
                    Text("Ayy Lmao")
                }
            }
        }
    }
}