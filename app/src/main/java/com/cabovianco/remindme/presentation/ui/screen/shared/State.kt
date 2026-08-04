package com.cabovianco.remindme.presentation.ui.screen.shared

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cabovianco.remindme.R

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    StatusLayout(modifier) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(modifier: Modifier = Modifier) {
    InfoState(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.main_error_state_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.error
            )
        },
        description = {
            Text(
                text = stringResource(R.string.main_error_state_description),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0.7f)
            )
        },
        icon = {
            Icon(
                modifier = Modifier
                    .size(120.dp)
                    .alpha(0.8f),
                painter = painterResource(R.drawable.illustration_error),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        }
    )
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    InfoState(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.main_empty_state_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        },
        description = {
            EmptyStateDescription()
        }
    )
}

@Composable
private fun EmptyStateDescription() {
    val description = stringResource(R.string.main_empty_state_description)
    val color = LocalContentColor.current.copy(alpha = 0.7f)

    val parts = description.split("%1\$s", limit = 2)
    val before = parts.getOrElse(0) { "" }
    val after = parts.getOrElse(1) { "" }

    val annotatedText = buildAnnotatedString {
        append(before.trimEnd())
        if (parts.size > 1) {
            append(" ")
            appendInlineContent("icon", "[icon]")
            append(" ")
            append(after.trimStart())
        }
    }

    val inlineContent = mapOf(
        "icon" to InlineTextContent(
            placeholder = Placeholder(
                width = 20.sp,
                height = 20.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
            )
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(1.dp, color.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = color
                )
            }
        }
    )

    Text(
        text = annotatedText,
        inlineContent = inlineContent,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = color
    )
}

@Composable
private fun InfoState(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    description: @Composable (() -> Unit)? = null,
    icon: @Composable (() -> Unit)? = null
) {
    StatusLayout(modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon?.invoke()

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                title()

                description?.invoke()
            }
        }
    }
}

@Composable
private fun StatusLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = BiasAlignment(0f, -0.4f)
    ) {
        content()
    }
}
