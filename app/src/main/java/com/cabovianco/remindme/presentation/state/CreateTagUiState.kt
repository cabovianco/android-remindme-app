package com.cabovianco.remindme.presentation.state

import com.cabovianco.remindme.domain.model.TagColor
import com.cabovianco.remindme.domain.model.TagIcon

data class CreateTagUiState(
    val name: String = "",
    val color: TagColor = TagColor.Blue,
    val icon: TagIcon? = null
) {
    val isValid: Boolean get() = name.isNotBlank()
}
