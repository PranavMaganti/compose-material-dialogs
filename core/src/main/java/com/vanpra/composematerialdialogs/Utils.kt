package com.vanpra.composematerialdialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
internal fun getString(@StringRes res: Int? = null, default: String? = null): String {
    return if (res != null) {
        LocalContext.current.getString(res)
    } else {
        default
            ?: throw IllegalArgumentException("Function must receive one non null string parameter")
    }
}

internal fun List<Pair<MaterialDialogButtonTypes, Placeable>>.buttons(
    type: MaterialDialogButtonTypes
) =
    this.filter { it.first == type }.map { it.second }

@Composable
internal fun isSmallDevice(): Boolean {
    return LocalConfiguration.current.screenWidthDp <= 360
}

operator fun Int.times(dp: Dp): Dp {
    return (this * dp.value).dp
}

@Composable
internal fun PaddingValues.getHorizontal(): PaddingValues =
    PaddingValues(
        start = this.calculateStartPadding(LocalLayoutDirection.current),
        end = this.calculateEndPadding(LocalLayoutDirection.current)
    )

data class PaddingValuesPx(
    val start: Int,
    val end: Int,
    val top: Int,
    val bottom: Int
) {
    fun totalVerticalPadding() = top + bottom

    fun totalHorizontalPadding() = start + end
}

@Composable
internal fun PaddingValues.roundToPx(density: Density): PaddingValuesPx =
    with(density) {
        PaddingValuesPx(
            start = calculateStartPadding(LocalLayoutDirection.current).roundToPx(),
            end = calculateEndPadding(LocalLayoutDirection.current).roundToPx(),
            top = calculateTopPadding().roundToPx(),
            bottom = calculateBottomPadding().roundToPx()
        )
    }
