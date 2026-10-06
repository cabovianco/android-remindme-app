package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderHistoryEntry
import com.cabovianco.remindme.domain.model.ReminderPriority
import com.cabovianco.remindme.domain.model.Tag
import javax.inject.Inject

class FilterRemindersUseCase @Inject constructor() {
    operator fun <T> invoke(
        items: List<T>,
        selectedTags: Set<Tag>,
        selectedPriority: ReminderPriority?
    ): List<T> = items.filter { item ->
        val tags = when (item) {
            is Reminder -> item.tags
            is ReminderHistoryEntry -> item.tags
            else -> emptyList()
        }

        val priority = when (item) {
            is Reminder -> item.priority
            is ReminderHistoryEntry -> item.priority
            else -> null
        }

        val tagFilterMatch = selectedTags.isEmpty() ||
                selectedTags.any { selectedTag ->
                    tags.any { it.id == selectedTag.id }
                }

        val priorityFilterMatch = selectedPriority == null ||
                priority == selectedPriority

        tagFilterMatch && priorityFilterMatch
    }
}
