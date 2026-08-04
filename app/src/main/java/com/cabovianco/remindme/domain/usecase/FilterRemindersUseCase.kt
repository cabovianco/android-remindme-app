package com.cabovianco.remindme.domain.usecase

import com.cabovianco.remindme.domain.model.Reminder
import com.cabovianco.remindme.domain.model.ReminderPriority
import com.cabovianco.remindme.domain.model.Tag
import javax.inject.Inject

class FilterRemindersUseCase @Inject constructor() {
    operator fun invoke(
        reminders: List<Reminder>,
        selectedTags: Set<Tag>,
        selectedPriority: ReminderPriority?
    ): List<Reminder> {
        return reminders.filter { reminder ->
            val tagFilterMatch = selectedTags.isEmpty() ||
                    selectedTags.any { selectedTag ->
                        reminder.tags.any { it.id == selectedTag.id }
                    }

            val priorityFilterMatch = selectedPriority == null ||
                    reminder.priority == selectedPriority

            tagFilterMatch && priorityFilterMatch
        }
    }
}
