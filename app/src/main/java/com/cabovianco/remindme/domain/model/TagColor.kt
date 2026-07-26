package com.cabovianco.remindme.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

val TAG_COLORS
    get() = listOf(
        TagColor.Blue,
        TagColor.LightBlue,
        TagColor.Green,
        TagColor.LightGreen,
        TagColor.Red,
        TagColor.Orange
    )

@Serializable
sealed class TagColor(
    val foreground: Long,
    val background: Long
) {
    @Serializable
    @SerialName("red")
    object Red : TagColor(foreground = 0xFFFFFFFF, background = 0xFF984343)

    @Serializable
    @SerialName("orange")
    object Orange : TagColor(foreground = 0xFFFFFFFF, background = 0xFFb46a4f)

    @Serializable
    @SerialName("green")
    object Green : TagColor(foreground = 0xFFa8b58a, background = 0xFF4e5a46)

    @Serializable
    @SerialName("light_green")
    object LightGreen : TagColor(foreground = 0xFF4e5a46, background = 0xFFa8b58a)

    @Serializable
    @SerialName("blue")
    object Blue : TagColor(foreground = 0xFFa7bbc6, background = 0xFF3b4651)

    @Serializable
    @SerialName("light_blue")
    object LightBlue : TagColor(foreground = 0xFF3b4651, background = 0xFFa7bbc6)
}
