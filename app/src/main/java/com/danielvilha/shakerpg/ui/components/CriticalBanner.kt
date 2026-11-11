package com.danielvilha.shakerpg.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danielvilha.shakerpg.ui.theme.CritFailRed
import com.danielvilha.shakerpg.ui.theme.CritFailRedDark
import com.danielvilha.shakerpg.ui.theme.CritSuccessGold
import com.danielvilha.shakerpg.ui.theme.GoldDark

@Composable
fun CriticalBanner(
    isCritSuccess: Boolean,
    isCritFail: Boolean,
    modifier: Modifier = Modifier
) {
    val isVisible = isCritSuccess || isCritFail

    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        val (bgBrush, borderColor, icon, title, subtitle) = if (isCritSuccess) {
            Tuple5(
                Brush.horizontalGradient(
                    listOf(
                        GoldDark.copy(alpha = 0.85f),
                        CritSuccessGold.copy(alpha = 0.95f),
                        GoldDark.copy(alpha = 0.85f)
                    )
                ),
                CritSuccessGold,
                Icons.Default.AutoAwesome,
                "NATURAL 20 — CRITICAL HIT!",
                "Triumph! Maximum effectiveness & bonus damage!"
            )
        } else {
            Tuple5(
                Brush.horizontalGradient(
                    listOf(
                        CritFailRedDark.copy(alpha = 0.9f),
                        CritFailRed.copy(alpha = 0.95f),
                        CritFailRedDark.copy(alpha = 0.9f)
                    )
                ),
                CritFailRed,
                Icons.Default.Warning,
                "NATURAL 1 — CRITICAL FAIL!",
                "Catastrophic fumble! Fate turns against you!"
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .background(bgBrush, shape = RoundedCornerShape(12.dp))
                .border(1.5.dp, borderColor, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .testTag("critical_banner"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
