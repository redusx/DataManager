package com.redusx.floatvault.ui.component

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.redusx.floatvault.ui.theme.DarkSuccess
import com.redusx.floatvault.ui.theme.LightSuccess
import com.redusx.floatvault.ui.theme.Spacing
import kotlinx.coroutines.delay

@Composable
fun CopyButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    compact: Boolean = false
) {
    val haptic = LocalHapticFeedback.current
    var isCopied by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.93f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "CopyButtonScale"
    )

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(1200)
            isCopied = false
        }
    }

    val buttonSize = if (compact) 30.dp else 38.dp
    val touchSize = if (compact) 32.dp else Spacing.touchTargetMin
    val iconSize = if (compact) 15.dp else 18.dp

    Box(
        modifier = modifier
            .size(touchSize)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClickLabel = contentDescription,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    isCopied = true
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(buttonSize)
                .scale(scale),
            shape = CircleShape,
            color = containerColor
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(buttonSize)
            ) {
                Crossfade(
                    targetState = isCopied,
                    label = "CopyIconCrossfade"
                ) { copied ->
                    if (copied) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = DarkSuccess,
                            modifier = Modifier.size(iconSize)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.ContentCopy,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }
            }
        }
    }
}
