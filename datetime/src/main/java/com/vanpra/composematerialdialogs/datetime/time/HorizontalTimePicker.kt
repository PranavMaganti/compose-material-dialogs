package com.vanpra.composematerialdialogs.datetime.time
//
//import androidx.compose.animation.Crossfade
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.CornerSize
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.unit.dp
//import com.vanpra.composematerialdialogs.datetime.util.isAM
//import com.vanpra.composematerialdialogs.datetime.util.toAM
//import com.vanpra.composematerialdialogs.datetime.util.toPM
//
//@Composable
//internal fun HorizontalTimePickerImpl(
//    modifier: Modifier = Modifier, title: String, state: TimePickerState
//) {
//    Column(modifier.padding(start = 24.dp, end = 24.dp)) {
//        Box(Modifier.align(Alignment.Start)) {
//            TimePickerTitle(Modifier.height(36.dp), title, state)
//        }
//
//        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
//            Column(
//                Modifier
//                    .padding(top = 72.dp, bottom = 50.dp)
//                    .width(216.dp)
//            ) {
//                TimeLayout(state = state)
//                Spacer(modifier = Modifier.height(12.dp))
//                HorizontalPeriodPicker(state = state)
//            }
//
//            /* This isn't an exact match to the material spec as there is a contradiction it.
//            Dialogs should be limited to the size of 560 dp but given sizes for extended
//            time picker go over this limit */
//            Spacer(modifier = Modifier.width(40.dp))
//            Crossfade(state.currentScreen) {
//                when (it) {
//                    ClockScreen.Hour -> if (state.is24Hour) {
//                        ExtendedClockHourLayout(state = state)
//                    } else {
//                        ClockHourLayout(state = state)
//                    }
//
//                    ClockScreen.Minute -> ClockMinuteLayout(state = state)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun HorizontalPeriodPicker(state: TimePickerState) {
//    val leftPeriodShape = MaterialTheme.shapes.medium.copy(
//        bottomEnd = CornerSize(0.dp), topEnd = CornerSize(0.dp)
//    )
//    val rightPeriodShape = MaterialTheme.shapes.medium.copy(
//        topStart = CornerSize(0.dp), bottomStart = CornerSize(0.dp)
//    )
//    val isAMEnabled = remember(state.timeRange) { state.timeRange.start.hour <= 12 }
//    val isPMEnabled = remember(state.timeRange) { state.timeRange.endInclusive.hour >= 0 }
//
//    Spacer(modifier = Modifier.width(12.dp))
//
//    Row(
//        Modifier
//            .fillMaxWidth()
//            .height(height = 40.dp)
//            .border(state.colors.border, MaterialTheme.shapes.medium)
//    ) {
//        Box(modifier = Modifier
//            .fillMaxHeight()
//            .fillMaxWidth(0.5f)
//            .clip(leftPeriodShape)
//            .background(state.colors.periodContainer(state.selectedTime.isAM).value)
//            .then(if (isAMEnabled) {
//                Modifier.clickable {
//                    state.selectedTime = state.selectedTime
//                        .toAM()
//                        .coerceIn(state.timeRange)
//                }
//            } else {
//                Modifier
//            }), contentAlignment = Alignment.Center) {
//            Text(
//                "AM",
//                color = state.colors.periodText(state.selectedTime.isAM).value,
//                style = TextStyle(
////                        .copy(alpha = if (isAMEnabled) ContentAlpha.high else ContentAlpha.disabled)
//                )
//            )
//        }
//
//        Spacer(
//            Modifier.fillMaxHeight().width(1.dp).background(state.colors.border.brush)
//        )
//
//        Box(modifier = Modifier.fillMaxSize().clip(rightPeriodShape)
//            .background(state.colors.periodBackgroundColor(!state.selectedTime.isAM).value)
//            .then(if (isPMEnabled) {
//                Modifier.clickable {
//                    state.selectedTime = state.selectedTime.toPM().coerceIn(state.timeRange)
//                }
//            } else {
//                Modifier
//            }), contentAlignment = Alignment.Center) {
//            Text(
//                "PM", style = TextStyle(
//                    state.colors.textColor(!state.selectedTime.isAM).value
////                        .copy(
////                        alpha = if (isPMEnabled) ContentAlpha.high else ContentAlpha.disabled
////                    )
//                )
//            )
//        }
//    }
//}
