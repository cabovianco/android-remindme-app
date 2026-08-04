package com.cabovianco.remindme.presentation.ui.screen.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cabovianco.remindme.R
import com.cabovianco.remindme.domain.model.ReminderPriority

@Composable
fun PriorityChip(
    priority: ReminderPriority,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = priority.toColor()

    val backgroundColor = when {
        isSelected -> color.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surfaceContainer
    }

    val contentColor = when {
        isSelected -> color
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val border = when {
        isSelected -> BorderStroke(1.dp, color)
        else -> BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.3f)
        )
    }

    Surface(
        modifier = modifier
            .widthIn(min = 80.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        contentColor = contentColor,
        border = border
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(priority.toResId()),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

fun ReminderPriority.toColor(): Color = when (this) {
    ReminderPriority.LOW -> Color(0xFFa8b58a)
    ReminderPriority.MEDIUM -> Color(0xFFb46a4f)
    ReminderPriority.HIGH -> Color(0xFF984343)
}

fun ReminderPriority.toResId(): Int = when (this) {
    ReminderPriority.LOW -> R.string.priority_low
    ReminderPriority.MEDIUM -> R.string.priority_medium
    ReminderPriority.HIGH -> R.string.priority_high
}
