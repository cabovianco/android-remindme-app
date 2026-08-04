package com.cabovianco.remindme.presentation.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.cabovianco.remindme.R
import com.cabovianco.remindme.domain.model.TAG_COLORS
import com.cabovianco.remindme.domain.model.TAG_ICONS
import com.cabovianco.remindme.domain.model.TagColor
import com.cabovianco.remindme.domain.model.TagIcon
import com.cabovianco.remindme.presentation.ui.screen.shared.AppButton
import com.cabovianco.remindme.presentation.ui.screen.shared.HorizontalSelector
import com.cabovianco.remindme.presentation.ui.screen.shared.NavigationTopBar
import com.cabovianco.remindme.presentation.ui.screen.shared.form.AppTextField
import com.cabovianco.remindme.presentation.ui.screen.shared.toResId
import com.cabovianco.remindme.presentation.viewmodel.CreateTagViewModel

@Composable
fun CreateTagScreen(
    onBackClick: () -> Unit,
    viewModel: CreateTagViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            NavigationTopBar(
                title = stringResource(R.string.create_tag_title),
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(16.dp),
                text = stringResource(R.string.common_btn_save),
                enabled = uiState.isValid,
                onClick = {
                    viewModel.onCreateTag()
                    onBackClick()
                }
            )
        }
    ) { padding ->
        CreateTagContent(
            name = uiState.name,
            onNameChange = { viewModel.onNameChange(it) },
            icon = uiState.icon,
            onIconChange = { viewModel.onIconChange(it) },
            color = uiState.color,
            onColorChange = { viewModel.onColorChange(it) },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun CreateTagContent(
    name: String,
    onNameChange: (String) -> Unit,
    icon: TagIcon?,
    onIconChange: (TagIcon?) -> Unit,
    color: TagColor,
    onColorChange: (TagColor) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AppTextField(
            value = name,
            onValueChange = onNameChange,
            label = stringResource(R.string.create_tag_name_hint),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        TagIconSelector(
            icon = icon,
            onIconChange = onIconChange,
            color = color
        )

        TagColorSelector(
            color = color,
            onColorChange = onColorChange
        )
    }
}

@Composable
private fun TagIconSelector(
    icon: TagIcon?,
    onIconChange: (TagIcon?) -> Unit,
    color: TagColor,
    modifier: Modifier = Modifier
) {
    HorizontalSelector(
        modifier = modifier,
        label = stringResource(R.string.create_tag_icon_label),
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_tag_icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        options = TAG_ICONS,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconItem(
            icon = it,
            isSelected = icon == it,
            color = Color(color.background),
            onClick = {
                if (icon == it) onIconChange(null)
                else onIconChange(it)
            }
        )
    }
}

@Composable
private fun TagColorSelector(
    color: TagColor,
    onColorChange: (TagColor) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalSelector(
        modifier = modifier,
        label = stringResource(R.string.create_tag_color_label),
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_tag_color),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        options = TAG_COLORS,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        ColorItem(
            color = it,
            isSelected = color == it,
            onClick = { onColorChange(it) }
        )
    }
}

@Composable
private fun IconItem(
    icon: TagIcon,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) color.copy(alpha = 0.25f) else Color.Transparent
    val contentColor = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant

    SelectableCircleContainer(
        onClick = onClick,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon.toResId()),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun ColorItem(
    color: TagColor,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = Color(color.background)

    SelectableCircleContainer(
        onClick = onClick,
        modifier = modifier
    ) {
        if (isSelected) {
            Canvas(modifier = Modifier.size(48.dp)) {
                val radius = size.minDimension / 2
                val strokeWidth = 4.dp.toPx()
                val gapWidth = 4.dp.toPx()

                drawCircle(
                    color = color,
                    radius = radius - strokeWidth / 2,
                    style = Stroke(width = strokeWidth)
                )

                drawCircle(
                    color = color,
                    radius = radius - strokeWidth - gapWidth
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
private fun SelectableCircleContainer(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
