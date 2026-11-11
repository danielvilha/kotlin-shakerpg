package com.danielvilha.shakerpg.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danielvilha.shakerpg.model.DieInstance
import com.danielvilha.shakerpg.ui.theme.CritFailRed
import com.danielvilha.shakerpg.ui.theme.CritSuccessGold
import com.danielvilha.shakerpg.ui.theme.GoldPrimary
import com.danielvilha.shakerpg.ui.theme.TextSecondary
import com.danielvilha.shakerpg.ui.theme.TextTertiary
import com.danielvilha.shakerpg.ui.theme.TrayFeltBorder
import com.danielvilha.shakerpg.ui.theme.TrayFeltDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiceArenaView(
    dice: List<DieInstance>,
    isRolling: Boolean,
    shakeIntensity: Float,
    isShakeEnabled: Boolean,
    onArenaTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val arenaBorderColor by animateColorAsState(
        targetValue = when {
            isRolling -> GoldPrimary
            shakeIntensity > 0.5f -> GoldPrimary.copy(alpha = 0.8f)
            else -> TrayFeltBorder
        },
        label = "arena_border_color"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp, max = 340.dp)
            .shadow(10.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF201A35),
                        TrayFeltDark,
                        Color(0xFF0E0B17)
                    )
                )
            )
            .border(2.dp, arenaBorderColor, RoundedCornerShape(20.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = GoldPrimary.copy(alpha = 0.3f)),
                onClick = onArenaTap
            )
            .testTag("dice_arena_tray"),
        contentAlignment = Alignment.Center
    ) {
        // Decorative corner brass studs
        Canvas(modifier = Modifier.matchParentSize()) {
            val studRadius = 5.dp.toPx()
            val offset = 14.dp.toPx()
            val studColor = Color(0xFFC79512)
            drawCircle(studColor, studRadius, Offset(offset, offset))
            drawCircle(studColor, studRadius, Offset(size.width - offset, offset))
            drawCircle(studColor, studRadius, Offset(offset, size.height - offset))
            drawCircle(studColor, studRadius, Offset(size.width - offset, size.height - offset))
        }

        if (dice.isEmpty()) {
            // Empty state placeholder
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White.copy(alpha = 0.06f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Casino,
                        contentDescription = "Empty dice tray",
                        tint = GoldPrimary.copy(alpha = 0.7f),
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Dice Tray is Empty",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap any polyhedral die below to build your pool, then shake or tap to roll!",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        } else {
            // Active rolling dice display
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.Center,
                    maxItemsInEachRow = if (dice.size <= 4) 2 else if (dice.size <= 8) 3 else 4,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    dice.forEach { die ->
                        val dieSize = when {
                            dice.size == 1 -> 110.dp
                            dice.size <= 3 -> 90.dp
                            dice.size <= 6 -> 78.dp
                            else -> 66.dp
                        }
                        Box(modifier = Modifier.padding(6.dp)) {
                            DieView(die = die, size = dieSize)
                        }
                    }
                }
            }
        }

        // Bottom shake feedback bar & hint
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = null,
                        tint = if (isShakeEnabled) GoldPrimary else TextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isShakeEnabled) "Shake phone or tap tray to roll" else "Tap tray or button to roll",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (isShakeEnabled && shakeIntensity > 0.05f) {
                    LinearProgressIndicator(
                        progress = { shakeIntensity },
                        modifier = Modifier
                            .width(60.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = GoldPrimary,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}
