package com.example.unusuallycomposedlist.view

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.ContentScale
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
        var count by remember { mutableStateOf(0) }

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

            Box(
                Modifier
                    .background(Color.Green)
                    .size(100.dp)
                    .clickable { count += 1 }) {
                Text(text = "$count")
            }

            VerticalListCoordinatedScrollImposer(
                modifier = Modifier
                    .gesturesDisabled(true),
                currScrollPerc = horizontalScrollOffsetPerc,
                otherListCurrPos = horizontalPos,
                contents = { verticalListState ->
                    LazyColumn(modifier = Modifier.fillMaxHeight(), state = verticalListState) {
                        itemsIndexed(itemsList) { _, item ->
                            var textContainerHeight by remember { mutableStateOf(0) }
                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillParentMaxHeight()
                            ) {
                                VerticalText(
                                    modifier = Modifier.onGloballyPositioned {
                                        textContainerHeight = it.size.height
                                    },
                                    text = item.title,
                                    textContainerHeight = textContainerHeight
                                )
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
    fun VerticalText(modifier: Modifier, text: String, textContainerHeight: Int) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = modifier
                    .rotate(-90f)
                    .requiredWidth(textContainerHeight.dp)
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

    fun Modifier.gesturesDisabled(disabled: Boolean = true) =
        pointerInput(disabled) {
            // if gestures enabled, we don't need to interrupt
            if (!disabled) return@pointerInput

            awaitPointerEventScope {
                // we should wait for all new pointer events
                while (true) {
                    awaitPointerEvent(pass = PointerEventPass.Initial)
                        .changes
                        .forEach(PointerInputChange::consumeAllChanges)
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