package com.example.unusuallycomposedlist.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
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
import kotlinx.coroutines.launch

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
        var horizontalListWidth by remember { mutableStateOf(1) }//1 to avoid crash on division
        var verticalListHeight by remember { mutableStateOf(1) }
        val coroutineScope = rememberCoroutineScope()

        Box(Modifier.fillMaxSize()) {
            ItemsSnapHelper(
                isScrollingHorizontally = true,
                contents = { horizontalListState ->
                    mainViewModel.scrollOffsetPerc.value =
                        (horizontalListState.firstVisibleItemScrollOffset * 100) / horizontalListWidth

                    LazyRow(modifier = Modifier.fillMaxSize(), state = horizontalListState) {
                        itemsIndexed(itemsList) { _, item ->
                            Box(modifier = Modifier
                                .fillParentMaxSize()
                                .onGloballyPositioned {
                                    horizontalListWidth = it.size.width
                                }
                            ) {
                                HorizontalItem(mainViewModel.scrollOffsetPerc.value.toString())
                            }
                        }
                    }
                }
            )

            //val verticalListState = rememberLazyListState()
            ItemsSnapHelper(
                isScrollingHorizontally = false,
                contents = { verticalListState ->

                    mainViewModel.scrollOffsetPerc.observe(this@MainActivity) { perc ->
                        coroutineScope.launch {
                            //val currScrollPx = verticalListState.firstVisibleItemScrollOffset
                            verticalListState.layoutInfo.visibleItemsInfo.firstOrNull()
                                ?.let { firstVisibleItem ->
                                    val verticalScrollPx = ((perc * verticalListHeight) / 100f)
                                    val currScrollPx = verticalListState.firstVisibleItemScrollOffset
                                    Log.d(
                                        "TAG",
                                        "[$perc][${firstVisibleItem.index}] verticalScrollPx:$verticalScrollPx currScrollPx:$currScrollPx => ${verticalScrollPx - currScrollPx}"
                                    )
                                    verticalListState.scrollBy(verticalScrollPx - currScrollPx)
                                }
                        }
                    }

                    LazyColumn(modifier = Modifier.fillMaxHeight(), state = verticalListState) {
                        itemsIndexed(itemsList) { _, item ->
                            var textContainerHeight by remember { mutableStateOf(0) }

                            BoxWithConstraints(modifier = Modifier
                                .fillParentMaxHeight()
                                .onGloballyPositioned { verticalListHeight = it.size.height }
                            ) {
                                VerticalItem(
                                    modifier = Modifier.onGloballyPositioned {
                                        textContainerHeight = it.size.height
                                    },
                                    item = item,
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
    fun VerticalItem(modifier: Modifier, item: String, textContainerHeight: Int) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = modifier
                    .rotate(-90f)
                    .requiredWidth(textContainerHeight.dp)
            ) {
                Text(
                    text = item,
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