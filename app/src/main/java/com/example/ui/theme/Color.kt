package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Primary theme colors
val PrimaryIndigo = Color(0xFF4F46E5)
val PrimaryIndigoDark = Color(0xFF818CF8)
val SecondaryTeal = Color(0xFF0D9488)
val SecondaryTealDark = Color(0xFF2DD4BF)
val AccentAmber = Color(0xFFD97706)

val BackgroundLight = Color(0xFFF8FAFC)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceVariantLight = Color(0xFFF1F5F9)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF64748B)

val BackgroundDark = Color(0xFF0F172A)
val SurfaceDark = Color(0xFF1E293B)
val SurfaceVariantDark = Color(0xFF334155)
val TextPrimaryDark = Color(0xFFF8FAFC)
val TextSecondaryDark = Color(0xFF94A3B8)

// Note pastel color palette
data class NoteColorOption(
    val hex: String,
    val name: String,
    val lightColor: Color,
    val darkColor: Color,
    val borderColor: Color
)

val NoteColors = listOf(
    NoteColorOption(
        hex = "#FFFFFF",
        name = "Default",
        lightColor = Color(0xFFFFFFFF),
        darkColor = Color(0xFF1E293B),
        borderColor = Color(0xFFE2E8F0)
    ),
    NoteColorOption(
        hex = "#EDE9FE",
        name = "Lavender",
        lightColor = Color(0xFFF5F3FF),
        darkColor = Color(0xFF2E1065),
        borderColor = Color(0xFFDDD6FE)
    ),
    NoteColorOption(
        hex = "#FEF3C7",
        name = "Warm Amber",
        lightColor = Color(0xFFFFFBEB),
        darkColor = Color(0xFF451A03),
        borderColor = Color(0xFFFDE68A)
    ),
    NoteColorOption(
        hex = "#E0F2FE",
        name = "Sky Blue",
        lightColor = Color(0xFFF0F9FF),
        darkColor = Color(0xFF082F49),
        borderColor = Color(0xFFBAE6FD)
    ),
    NoteColorOption(
        hex = "#D1FAE5",
        name = "Mint Green",
        lightColor = Color(0xFFECFDF5),
        darkColor = Color(0xFF064E3B),
        borderColor = Color(0xFFA7F3D0)
    ),
    NoteColorOption(
        hex = "#FFE4E6",
        name = "Soft Rose",
        lightColor = Color(0xFFFFF1F2),
        darkColor = Color(0xFF4C0519),
        borderColor = Color(0xFFFECDD3)
    ),
    NoteColorOption(
        hex = "#FFEDD5",
        name = "Peach",
        lightColor = Color(0xFFFFF7ED),
        darkColor = Color(0xFF431407),
        borderColor = Color(0xFFFED7AA)
    ),
    NoteColorOption(
        hex = "#F1F5F9",
        name = "Slate Grey",
        lightColor = Color(0xFFF8FAFC),
        darkColor = Color(0xFF0F172A),
        borderColor = Color(0xFFCBD5E1)
    )
)

fun getNoteColorOption(hex: String): NoteColorOption {
    return NoteColors.find { it.hex.equals(hex, ignoreCase = true) } ?: NoteColors.first()
}
