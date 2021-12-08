package com.example.unusuallycomposedlist.view

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private val mainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                UnusualList()
                LoadingDialog()
            }
        }

        mainViewModel.generateItemsList(this)
    }

    @ExperimentalComposeUiApi
    @Composable
    fun UnusualList() {
        val itemsList by mainViewModel.itemsList.observeAsState(listOf())
        var horizontalScrollOffsetPerc by remember { mutableStateOf(0f) }
        var horizontalPos by remember { mutableStateOf(0) }

        Box(Modifier.fillMaxSize()) {

            ItemsSnapHelper(
                isScrollingHorizontally = true,
                onScroll = { scrollPerc, pos ->
                    horizontalScrollOffsetPerc = scrollPerc
                    horizontalPos = pos
                },
                contents = { horizontalListState ->
                    LazyRow(modifier = Modifier.fillMaxSize(), state = horizontalListState) {
                        itemsIndexed(itemsList) { _, item ->
                            Box(modifier = Modifier.fillParentMaxSize()) {
                                FullScreenPicture(bitmap = item.image)
                            }
                        }
                    }
                }
            )

            VerticalListCoordinatedScrollImposer(
                currScrollPerc = horizontalScrollOffsetPerc,
                otherListCurrPos = horizontalPos,
                contents = { verticalListState ->
                    LazyColumn(
                        modifier = Modifier.fillMaxHeight(),
                        state = verticalListState
                    ) {
                        itemsIndexed(itemsList) { _, item ->
                            BoxWithConstraints(modifier = Modifier.fillParentMaxHeight()) {
                                VerticalText(text = item.title)
                            }
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun FullScreenPicture(bitmap: Bitmap) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(secondaryLight),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.FillHeight
            )
        }
    }

    @Composable
    fun VerticalText(modifier: Modifier = Modifier, text: String) {
        var containerHeightDp by remember { mutableStateOf(0) }
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxHeight()
                .width(100.dp),
            contentAlignment = Alignment.Center
        ) {
            containerHeightDp = maxHeight.value.roundToInt()
            Box(
                modifier = modifier
                    .rotate(-90f)
                    .requiredWidth(containerHeightDp.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    color = primaryVariantLight,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
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