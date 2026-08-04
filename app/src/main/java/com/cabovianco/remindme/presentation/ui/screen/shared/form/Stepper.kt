package com.cabovianco.remindme.presentation.ui.screen.shared.form

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cabovianco.remindme.R

@Composable
fun NumberStepper(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    range: IntRange = 1..99
) {
    Surface(
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StepperButton(
                painter = painterResource(R.drawable.ic_minus),
                enabled = value > range.first,
                onClick = { onValueChange(value - 1) }
            )

            Text(
                modifier = Modifier
                    .width(32.dp)
                    .padding(horizontal = 4.dp),
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            StepperButton(
                painter = painterResource(R.drawable.ic_plus),
                enabled = value < range.last,
                onClick = { onValueChange(value + 1) }
            )
        }
    }
}

@Composable
private fun StepperButton(
    painter: Painter,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier.size(28.dp),
        onClick = onClick,
        enabled = enabled,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            painter = painter,
            contentDescription = null
        )
    }
}
