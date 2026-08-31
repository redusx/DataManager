package com.example.datamanager.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * MyVault canonical 4dp spacing scale and accessibility touch target constants.
 */
object Spacing {
    /** 4dp - Micro gaps, icon-to-badge text */
    val xxs: Dp = 4.dp

    /** 8dp - Inter-chip gaps, compact vertical spacing */
    val xs: Dp = 8.dp

    /** 12dp - Form field gap, compact card padding */
    val s: Dp = 12.dp

    /** 16dp - Standard screen margin, card inner padding */
    val m: Dp = 16.dp

    /** 20dp - Section vertical separation */
    val l: Dp = 20.dp

    /** 24dp - Modal sheet padding, major header gaps */
    val xl: Dp = 24.dp

    /** 32dp - Keypad breathing room, empty state gaps */
    val xxl: Dp = 32.dp

    /** 48dp - Non-negotiable minimum accessible touch target width & height */
    val touchTargetMin: Dp = 48.dp
}
