package com.example.unusuallycomposedlist.view

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun ItemsSnapHelper(contents: @Composable (LazyListState) -> Unit) {
    var containerWidthPx by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                listState.layoutInfo.visibleItemsInfo.firstOrNull()
                    ?.let { firstVisibleItemInfo ->
                        val pos = if (abs(firstVisibleItemInfo.offset) <= containerWidthPx / 2) {
                            firstVisibleItemInfo.index
                        } else {
                            firstVisibleItemInfo.index + 1
                        }
                        coroutineScope.launch { listState.animateScrollToItem(pos) }
                    }
                return Velocity.Zero
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
        containerWidthPx = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
        contents(listState)
    }
}