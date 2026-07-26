package com.cabovianco.remindme.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val TAG_ICONS
    get() = listOf(
        TagIcon.Work,
        TagIcon.School,
        TagIcon.Fitness,
        TagIcon.Health,
        TagIcon.Food,
        TagIcon.Leisure,
        TagIcon.Shopping
    )

@Serializable
sealed interface TagIcon {
    @Serializable
    @SerialName("work")
    object Work : TagIcon

    @Serializable
    @SerialName("school")
    object School : TagIcon

    @Serializable
    @SerialName("fitness")
    object Fitness : TagIcon

    @Serializable
    @SerialName("health")
    object Health : TagIcon

    @Serializable
    @SerialName("food")
    object Food : TagIcon

    @Serializable
    @SerialName("leisure")
    object Leisure : TagIcon

    @Serializable
    @SerialName("shopping")
    object Shopping : TagIcon
}
