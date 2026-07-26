package com.cabovianco.remindme.presentation.state

import com.cabovianco.remindme.domain.model.ReminderRepeat
import com.cabovianco.remindme.domain.model.Tag
import java.time.ZonedDateTime

data class ReminderFormUiState(
    val id: Long = 0,
    val title: String = "",
    val description: String? = null,
    val dateTime: ZonedDateTime = ZonedDateTime.now()
        .plusHours(1)
        .withMinute(0)
        .withSecond(0)
        .withNano(0),
    val repeat: ReminderRepeat = ReminderRepeat.Never,
    val tags: List<Tag> = emptyList(),
    val selectedTags: Set<Tag> = emptySet()
) {
    val isValid: Boolean get() {
        val now = ZonedDateTime.now().withSecond(0).withNano(0)
        return title.isNotBlank() && dateTime.isAfter(now)
    }
}
