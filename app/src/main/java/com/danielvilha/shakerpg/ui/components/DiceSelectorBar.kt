package com.danielvilha.shakerpg.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danielvilha.shakerpg.model.RollMode
import com.danielvilha.shakerpg.model.RpgDieType
import com.danielvilha.shakerpg.ui.theme.BorderSubtle
import com.danielvilha.shakerpg.ui.theme.CritFailRed
import com.danielvilha.shakerpg.ui.theme.GoldPrimary
import com.danielvilha.shakerpg.ui.theme.SurfaceCard
import com.danielvilha.shakerpg.ui.theme.SurfaceCardElevated
import com.danielvilha.shakerpg.ui.theme.TextPrimary
import com.danielvilha.shakerpg.ui.theme.TextSecondary
import com.danielvilha.shakerpg.ui.theme.TextTertiary

@Composable
fun DiceSelectorBar(
    poolCounts: Map<RpgDieType, Int>,
    modifierValue: Int,
    rollMode: RollMode,
    onAddDie: (RpgDieType) -> Unit,
    onRemoveDie: (RpgDieType) -> Unit,
    onClearPool: () -> Unit,
    onIncrementMod: () -> Unit,
    onDecrementMod: () -> Unit,
    onOpenPresets: () -> Unit,
    onSelectRollMode: (RollMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(SurfaceCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .padding(14.dp)
            .testTag("dice_selector_bar")
    ) {
        // Top row: Modifier Stepper + Presets + Clear
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Modifier Stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(SurfaceCardElevated, RoundedCornerShape(12.dp))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                IconButton(
                    onClick = onDecrementMod,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("mod_minus_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease modifier",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = if (modifierValue >= 0) "MOD +$modifierValue" else "MOD $modifierValue",
                    color = GoldPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                IconButton(
                    onClick = onIncrementMod,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("mod_plus_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase modifier",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Presets Button & Clear Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceCardElevated)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                        .clickable(onClick = onOpenPresets)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("presets_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmarks,
                            contentDescription = "RPG Presets",
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Presets",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                val totalDice = poolCounts.values.sum()
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (totalDice > 0) CritFailRed.copy(alpha = 0.15f) else SurfaceCardElevated)
                        .border(
                            1.dp,
                            if (totalDice > 0) CritFailRed.copy(alpha = 0.4f) else BorderSubtle,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = totalDice > 0, onClick = onClearPool)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .testTag("clear_pool_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear pool",
                            tint = if (totalDice > 0) CritFailRed else TextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Clear",
                            color = if (totalDice > 0) CritFailRed else TextTertiary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Polyhedral Dice Selector Chips
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RpgDieType.entries.forEach { die ->
                val count = poolCounts[die] ?: 0
                DieSelectorItem(
                    dieType = die,
                    count = count,
                    onAdd = { onAddDie(die) },
                    onRemove = { onRemoveDie(die) }
                )
            }
        }
    }
}

@Composable
private fun DieSelectorItem(
    dieType: RpgDieType,
    count: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    val isSelected = count > 0

    Box(
        modifier = Modifier
            .size(width = 66.dp, height = 76.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) SurfaceCardElevated else Color(0xFF141021))
            .border(
                1.5.dp,
                if (isSelected) dieType.primaryColor else BorderSubtle,
                RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = dieType.primaryColor.copy(alpha = 0.3f)),
                onClick = onAdd
            )
            .testTag("selector_die_${dieType.label}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Miniature Polyhedral Die icon
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(dieType.primaryColor.copy(alpha = 0.2f), CircleShape)
                    .border(1.dp, dieType.primaryColor.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dieType.label,
                    color = dieType.primaryColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dieType.fullName.take(6),
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Count Badge
        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .size(20.dp)
                    .background(dieType.primaryColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$count",
                    color = Color.Black,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Mini remove trigger if count > 0 (tappable bottom edge)
        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(onClick = onRemove)
                    .padding(vertical = 1.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "-1",
                    color = CritFailRed,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
