package com.vanpra.composematerialdialogs.test.utils.extensions

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag

fun ComposeTestRule.onDialogDateSelector(date: Int) =
    this.onAllNodesWithTag("dialog_date_selection_$date")[1]
