package com.example.unusuallycomposedlist.view

import android.annotation.SuppressLint
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

private fun getHorizontalScrollOffsetPerc(horizontalListState: LazyListState, containerWidth: Int) =
    (horizontalListState.firstVisibleItemScrollOffset * 100f) / containerWidth

private fun getVerticalScrollOffsetPx(scrollPerc: Float, containerHeight: Int) =
    (scrollPerc * containerHeight) / 100f

private fun getFirstVisibleItemIndex(listState: LazyListState) =
    listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0

private fun getFirstVisibleItemOffset(listState: LazyListState) =
    listState.layoutInfo.visibleItemsInfo.firstOrNull()?.offset ?: 0

private fun getHalfwayLimit(
    isScrollingHorizontally: Boolean,
    containerWidth: Int,
    containerHeight: Int
): Float {
    return if (isScrollingHorizontally) {
        containerWidth / 2f
    } else {
        containerHeight / 2f
    }
}

private fun getCurrPos(listState: LazyListState, halfwayLimit: Float): Int {
    val firstVisibleItemOffset = getFirstVisibleItemOffset(listState)
    val firstVisibleItemIndex = getFirstVisibleItemIndex(listState)
    return if (abs(firstVisibleItemOffset) <= halfwayLimit) {
        firstVisibleItemIndex
    } else {
        firstVisibleItemIndex + 1
    }
}

@Composable
fun ItemsSnapHelper(
    isScrollingHorizontally: Boolean,
    onScroll: (Float, Int) -> Unit,
    contents: @Composable (LazyListState) -> Unit
) {
    var containerWidthPx by remember { mutableStateOf(0) }
    var containerHeightPx by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                val halfwayLimit = getHalfwayLimit(
                    isScrollingHorizontally = isScrollingHorizontally,
                    containerWidth = containerWidthPx,
                    containerHeight = containerHeightPx
                )
                val pos = getCurrPos(listState, halfwayLimit)
                coroutineScope.launch { listState.animateScrollToItem(pos) }
                return Velocity.Zero
            }
        }
    }

    BoxWithConstraints(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
        containerWidthPx = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
        containerHeightPx = with(LocalDensity.current) { maxHeight.toPx() }.toInt()
        onScroll(
            getHorizontalScrollOffsetPerc(listState, containerWidthPx),
            getFirstVisibleItemIndex(listState)
        )
        contents(listState)
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun VerticalListCoordinatedScrollImposer(
    modifier: Modifier=Modifier,
    currScrollPerc: Float,
    otherListCurrPos: Int,
    contents: @Composable (LazyListState) -> Unit
) {
    var containerHeightPx by remember { mutableStateOf(0) }
    val verticalListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val currColumnVerticalScrollPx = getVerticalScrollOffsetPx(currScrollPerc, containerHeightPx)
    val currScrollPx = verticalListState.firstVisibleItemScrollOffset
    val delta = currColumnVerticalScrollPx - currScrollPx
    if (verticalListState.firstVisibleItemIndex == otherListCurrPos) {
        coroutineScope.launch { verticalListState.dispatchRawDelta(delta) }
    } else {
        coroutineScope.launch { verticalListState.scrollToItem(otherListCurrPos) }
    }

    BoxWithConstraints(modifier = modifier) {
        containerHeightPx = with(LocalDensity.current) { maxHeight.toPx() }.toInt()
        contents(verticalListState)
    }
}
