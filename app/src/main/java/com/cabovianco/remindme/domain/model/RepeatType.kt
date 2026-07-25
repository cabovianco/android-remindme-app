package com.cabovianco.remindme.domain.model

enum class RepeatType {
    NEVER,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}

fun RepeatType.toRepeat() = when (this) {
    RepeatType.NEVER -> Repeat.Never
    RepeatType.DAILY -> Repeat.Daily
    RepeatType.WEEKLY -> Repeat.Weekly
    RepeatType.MONTHLY -> Repeat.Monthly
    RepeatType.YEARLY -> Repeat.Yearly
}
