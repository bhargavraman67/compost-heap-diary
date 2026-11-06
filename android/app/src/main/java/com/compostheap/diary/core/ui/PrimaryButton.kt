package com.compostheap.diary.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.compostheap.diary.core.theme.ButtonShape
import com.compostheap.diary.core.theme.Parchment
import com.compostheap.diary.core.theme.SoilDeep
import com.compostheap.diary.core.theme.SoilSurface
import com.compostheap.diary.core.theme.SproutDeep
import com.compostheap.diary.core.theme.SproutGreen

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    startColor: Color = SproutGreen,
    endColor: Color = SproutDeep
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 900f),
        label = "primaryPress"
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactionSource,
        shape = ButtonShape,
        elevation = null,
        contentPadding = PaddingValues(horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = SoilDeep,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Parchment.copy(alpha = 0.45f)
        ),
        modifier = modifier
            .height(64.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = ButtonShape,
                ambientColor = startColor,
                spotColor = startColor
            )
            .clip(ButtonShape)
            .background(
                if (enabled) {
                    Brush.horizontalGradient(listOf(startColor, endColor))
                } else {
                    Brush.horizontalGradient(listOf(SoilSurface, SoilSurface))
                }
            )
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
