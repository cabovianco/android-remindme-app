package com.cabovianco.remindme.presentation.ui.screen.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cabovianco.remindme.R

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    StatusLayout(modifier) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.common_error_generic)
) {
    InfoState(
        modifier = modifier,
        text = message,
        painter = painterResource(R.drawable.error_state),
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
fun EmptyState(
    text: String,
    painter: Painter,
    modifier: Modifier = Modifier
) {
    InfoState(
        modifier = modifier,
        text = text,
        painter = painter
    )
}

@Composable
private fun InfoState(
    text: String,
    painter: Painter,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) {
    StatusLayout(modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier
                    .size(58.dp)
                    .alpha(0.7f),
                painter = painter,
                contentDescription = null,
                tint = color.takeOrElse { LocalContentColor.current }
            )

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = color
            )
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
