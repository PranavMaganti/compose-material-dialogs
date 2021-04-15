package com.vanpra.composematerialdialogs.test.utils.extensions

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag

fun ComposeTestRule.onDialogDateSelector(date: Int) =
    this.onNodeWithTag("dialog_date_selection_$date")
