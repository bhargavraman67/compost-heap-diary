package com.compostheap.diary.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val OrganicShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 8.dp,
    bottomEnd = 20.dp,
    bottomStart = 8.dp
)

val ButtonShape = RoundedCornerShape(20.dp)

val ChipShape = RoundedCornerShape(14.dp)

val DialogShape = RoundedCornerShape(24.dp)

val PillShape = RoundedCornerShape(50)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = ChipShape,
    medium = OrganicShape,
    large = RoundedCornerShape(22.dp),
    extraLarge = DialogShape
)
