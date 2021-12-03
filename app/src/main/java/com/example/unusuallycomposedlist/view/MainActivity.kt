package com.example.unusuallycomposedlist.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider
import com.example.unusuallycomposedlist.theme.AppTheme
import com.example.unusuallycomposedlist.theme.primaryVariantLight
import com.example.unusuallycomposedlist.theme.secondaryLight
import com.example.unusuallycomposedlist.viewModel.MainViewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                UnusualList()
                LoadingDialog()
            }
        }

        mainViewModel.generateItemsList()
    }

    @Composable
    fun UnusualList() {
        val itemsList by mainViewModel.itemsList.observeAsState(listOf())
        val verticalTextFontSize = 50.sp

        Box(Modifier.fillMaxSize()) {
            ItemsSnapHelper(
                isScrollingHorizontally = true,
                contents = { listState ->
                    LazyRow(modifier = Modifier.fillMaxSize(), state = listState) {
                        itemsIndexed(itemsList) { _, item ->
                            Box(modifier = Modifier.fillParentMaxSize()) {
                                HorizontalItem(item)
                            }
                        }
                    }
                }
            )

            LazyColumn(modifier = Modifier.fillMaxHeight()) {
                itemsIndexed(itemsList) { _, item ->
                    var verticalTextHeight by remember { mutableStateOf(0) }
                    var verticalTextWidth by remember { mutableStateOf(0) }

                    Box(modifier = Modifier.fillParentMaxHeight()) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier
                                .rotate(-90f)
                                .requiredWidth(verticalTextHeight.dp)
                                .onGloballyPositioned {
                                    verticalTextHeight = it.size.height
                                    verticalTextWidth = it.size.width
                                }) {
                                Text(
                                    text = item,
                                    color = primaryVariantLight,
                                    fontSize = verticalTextFontSize,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                        /*
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .requiredWidth(100.dp)
                                .background(Color.Green)
                        ) {
                            Text(
                                modifier = Modifier
                                    .rotate(-90f)
                                    .offset { IntOffset(-verticalTextWidth / 2, 0) }
                                    .padding(vertical = 20.dp)
                                    .onGloballyPositioned {
                                        verticalTextWidth = it.size.height
                                    },
                                text = item,
                                color = primaryVariantLight,
                                fontSize = verticalTextFontSize,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                         */
                    }
                }
            }
        }
    }

    @Composable
    fun HorizontalItem(item: String) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(secondaryLight),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = item)
        }
    }

    @Composable
    fun LoadingDialog() {
        val isLoading by mainViewModel.isLoading.observeAsState(false)
        if (isLoading) {
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                ),
                content = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.White, shape = RoundedCornerShape(10.dp)),
                        content = { CircularProgressIndicator() }
                    )
                }
            )
        }
    }

}