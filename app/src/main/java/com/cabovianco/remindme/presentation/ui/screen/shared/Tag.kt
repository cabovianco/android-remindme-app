package com.cabovianco.remindme.presentation.ui.screen.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cabovianco.remindme.R
import com.cabovianco.remindme.domain.model.TagColor
import com.cabovianco.remindme.domain.model.TagIcon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    icon: TagIcon? = null,
    color: TagColor? = null
) {
    val backgroundColor = when {
        isSelected && color != null -> Color(color.background)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceContainer
    }

    val contentColor = when {
        isSelected && color != null -> Color(color.foreground)
        isSelected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val border = if (!isSelected) BorderStroke(
        1.dp,
        MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.3f)
    )
    else null

    Surface(
        modifier = modifier
            .widthIn(min = 80.dp)
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        contentColor = contentColor,
        border = border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            icon?.let {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(it.toResId()),
                    contentDescription = null,
                    tint = contentColor
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
fun CompactTagChip(
    text: String,
    modifier: Modifier = Modifier,
    icon: TagIcon? = null,
    color: TagColor? = null
) {
    val backgroundColor = color?.let { Color(it.background) } ?: MaterialTheme.colorScheme.primary
    val contentColor = color?.let { Color(it.foreground) } ?: MaterialTheme.colorScheme.onPrimary

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.let {
                Icon(
                    modifier = Modifier.size(12.dp),
                    painter = painterResource(it.toResId()),
                    contentDescription = null,
                    tint = contentColor
                )
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 100.dp)
            )
        }
    }
}

@Composable
fun CreateTagButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    AppIconButton(
        modifier = modifier,
        onClick = onClick,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null
            )
        },
        shape = CircleShape
    )
}

fun TagIcon.toResId(): Int = when (this) {
    TagIcon.Work -> R.drawable.ic_tag_work
    TagIcon.School -> R.drawable.ic_tag_school
    TagIcon.Fitness -> R.drawable.ic_tag_fitness
    TagIcon.Shopping -> R.drawable.ic_tag_shopping
    TagIcon.Health -> R.drawable.ic_tag_health
    TagIcon.Food -> R.drawable.ic_tag_food
    TagIcon.Leisure -> R.drawable.ic_tag_leisure
}
