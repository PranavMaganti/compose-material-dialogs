package com.vanpra.composematerialdialogs

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

/**
 *  Adds a title with the given text to the dialog
 * @param text title text from a string literal
 * @param res title text from a string resource
 * @param center text is aligned to center when true
 */
@Composable
fun MaterialDialogScope.title(
    text: String? = null,
    @StringRes res: Int? = null,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.headlineSmall,
    center: Boolean = false
) {
    val titleText = getString(res, text)

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .wrapContentHeight(Alignment.CenterVertically)
            .wrapContentWidth(
                if (center) Alignment.CenterHorizontally else Alignment.Start
            ), text = titleText, color = color, style = style
    )
    Spacer(modifier = Modifier.height(DialogConstants.TitleBodyPadding))
}

/**
 *  Adds a title with the given text and icon to the dialog
 * @param text title text from a string literal
 * @param textRes title text from a string resource
 * @param icon optional icon displayed at the start of the title
 */
@Composable
fun MaterialDialogScope.iconTitle(
    text: String? = null,
    @StringRes textRes: Int? = null,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.headlineSmall,
    icon: @Composable () -> Unit = {}
) {
    val titleText = getString(textRes, text)

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = DialogConstants.InternalPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.secondary) {
            icon()
        }

        Spacer(Modifier.height(DialogConstants.IconTitlePadding))
        Text(
            text = titleText, color = color, style = style
        )
    }

    Spacer(modifier = Modifier.height(DialogConstants.TitleBodyPadding))
}

/**
 *  Adds paragraph of text to the dialog
 * @param text message text from a string literal
 * @param res message text from a string resource
 */
@Composable
fun MaterialDialogScope.message(
    text: String? = null,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    @StringRes res: Int? = null
) {
    val messageText = getString(res, text)

    Text(
        modifier = Modifier.padding(horizontal = DialogConstants.InternalPadding),
        text = messageText,
        color = color,
        style = style,
    )
}