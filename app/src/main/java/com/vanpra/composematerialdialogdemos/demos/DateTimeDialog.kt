package com.vanpra.composematerialdialogdemos.demos

import androidx.compose.runtime.Composable
import com.vanpra.composematerialdialogdemos.DialogAndShowButton
import com.vanpra.composematerialdialogs.datetime.datepicker
import com.vanpra.composematerialdialogs.datetime.datetimepicker
import com.vanpra.composematerialdialogs.datetime.timepicker


/**
 * @brief Date and Time Picker Demos
 */
@Composable
fun DateTimeDialogDemo() {
    DialogAndShowButton(buttonText = "Time Picker Dialog") {
        timepicker()
    }

    DialogAndShowButton(buttonText = "Date Picker Dialog") {
        datepicker()
    }

    DialogAndShowButton(buttonText = "Date and Time Picker Dialog") {
        // Date time picker has a custom title as it needs to show a back icon
        datetimepicker()
    }
}