package com.example.datamanager.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val FloatVaultShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

val MyVaultShapes = FloatVaultShapes

object ShapeTokens {
    val BadgeRadius = RoundedCornerShape(4.dp)
    val ChipRadius = RoundedCornerShape(8.dp)
    val ButtonRadius = RoundedCornerShape(8.dp)
    val InputRadius = RoundedCornerShape(8.dp)
    val CardRadius = RoundedCornerShape(12.dp)
    val DialogRadius = RoundedCornerShape(12.dp)
    val SearchRadius = RoundedCornerShape(16.dp)
    val OverlayRadius = RoundedCornerShape(16.dp)
    val BottomSheetRadius = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val FullCircle = CircleShape
}
