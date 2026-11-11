package com.danielvilha.shakerpg.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danielvilha.shakerpg.ui.components.CriticalBanner
import com.danielvilha.shakerpg.ui.components.DiceArenaView
import com.danielvilha.shakerpg.ui.components.DiceSelectorBar
import com.danielvilha.shakerpg.ui.components.PresetsSheet
import com.danielvilha.shakerpg.ui.components.RollBreakdownCard
import com.danielvilha.shakerpg.ui.components.RollHistorySheet
import com.danielvilha.shakerpg.ui.components.SettingsSheet
import com.danielvilha.shakerpg.ui.theme.CritFailRed
import com.danielvilha.shakerpg.ui.theme.CritSuccessGold
import com.danielvilha.shakerpg.ui.theme.GoldDark
import com.danielvilha.shakerpg.ui.theme.GoldPrimary
import com.danielvilha.shakerpg.ui.theme.ObsidianBackground
import com.danielvilha.shakerpg.ui.theme.SurfaceCard
import com.danielvilha.shakerpg.ui.theme.SurfaceCardElevated
import com.danielvilha.shakerpg.ui.theme.TextPrimary
import com.danielvilha.shakerpg.ui.theme.TextSecondary
import com.danielvilha.shakerpg.viewmodel.DiceUiState
import com.danielvilha.shakerpg.viewmodel.DiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceScreen(
    viewModel: DiceViewModel,
    uiState: DiceUiState,
    shakeIntensity: Float,
    modifier: Modifier = Modifier
) {
    var showHistorySheet by remember { mutableStateOf(false) }
    var showPresetsSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground),
        containerColor = ObsidianBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(GoldDark.copy(alpha = 0.3f), CircleShape)
                                .border(1.5.dp, GoldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ShakeRPG",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Polyhedral Dice Roller",
                                color = GoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ObsidianBackground,
                    titleContentColor = TextPrimary
                ),
                actions = {
                    // History button with count badge
                    Box(contentAlignment = Alignment.TopEnd) {
                        IconButton(
                            onClick = { showHistorySheet = true },
                            modifier = Modifier.testTag("history_top_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Roll History",
                                tint = TextSecondary
                            )
                        }
                        if (uiState.rollHistory.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp, end = 6.dp)
                                    .size(16.dp)
                                    .background(GoldPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${uiState.rollHistory.size.coerceAtMost(99)}",
                                    color = Color.Black,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Settings button
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier.testTag("settings_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary
                        )
                    }
                },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top section: Banners & Arena & Breakdown
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Critical Callout Banner (Nat 20 / Nat 1)
                    val lastRecord = uiState.lastRollRecord
                    val isCritSuccess = (lastRecord?.isCritSuccess == true) && !uiState.isRolling
                    val isCritFail = (lastRecord?.isCritFail == true) && !uiState.isRolling

                    CriticalBanner(
                        isCritSuccess = isCritSuccess,
                        isCritFail = isCritFail
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Physical Dice Arena Tray
                    DiceArenaView(
                        dice = uiState.diceInstances,
                        isRolling = uiState.isRolling,
                        shakeIntensity = shakeIntensity,
                        isShakeEnabled = uiState.isShakeEnabled,
                        onArenaTap = { viewModel.rollDice() }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Grand Total & Breakdown Card
                    RollBreakdownCard(
                        lastRecord = uiState.lastRollRecord,
                        formula = uiState.formulaString,
                        modifierValue = uiState.modifier,
                        isRolling = uiState.isRolling,
                        rollMode = uiState.rollMode
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Prominent Roll Dice Button
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse_button")
                    val pulseScale by infiniteTransition.animateFloat(
                        initialValue = 1.0f,
                        targetValue = 1.03f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(900, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulse_scale"
                    )

                    Button(
                        onClick = { viewModel.rollDice() },
                        enabled = !uiState.isRolling,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color(0xFF1E1400),
                            disabledContainerColor = SurfaceCardElevated,
                            disabledContentColor = TextSecondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .scale(if (!uiState.isRolling) pulseScale else 1f)
                            .testTag("roll_dice_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Casino,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (uiState.isRolling) "TUMBLING..." else "ROLL ${uiState.formulaString.uppercase()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Bottom Section: Polyhedral Dice Selector Bar
                DiceSelectorBar(
                    poolCounts = uiState.poolCounts,
                    modifierValue = uiState.modifier,
                    rollMode = uiState.rollMode,
                    onAddDie = { viewModel.addDie(it) },
                    onRemoveDie = { viewModel.removeDie(it) },
                    onClearPool = { viewModel.clearPool() },
                    onIncrementMod = { viewModel.incrementModifier() },
                    onDecrementMod = { viewModel.decrementModifier() },
                    onOpenPresets = { showPresetsSheet = true },
                    onSelectRollMode = { viewModel.setRollMode(it) },
                    modifier = Modifier.navigationBarsPadding()
                )
            }
        }
    }

    // Modal Bottom Sheets
    if (showHistorySheet) {
        RollHistorySheet(
            history = uiState.rollHistory,
            stats = uiState.stats,
            onReRoll = { viewModel.reRollRecord(it) },
            onDeleteRoll = { viewModel.deleteRoll(it) },
            onClearHistory = { viewModel.clearHistory() },
            onDismiss = { showHistorySheet = false }
        )
    }

    if (showPresetsSheet) {
        PresetsSheet(
            presets = viewModel.availablePresets,
            onSelectPreset = { viewModel.applyPreset(it) },
            onDismiss = { showPresetsSheet = false }
        )
    }

    if (showSettingsSheet) {
        SettingsSheet(
            isShakeEnabled = uiState.isShakeEnabled,
            shakeSensitivity = uiState.shakeSensitivity,
            isHapticsEnabled = uiState.isHapticsEnabled,
            isSoundEnabled = uiState.isSoundEnabled,
            rollMode = uiState.rollMode,
            onToggleShake = { viewModel.toggleShakeEnabled(it) },
            onChangeSensitivity = { viewModel.updateShakeSensitivity(it) },
            onToggleHaptics = { viewModel.toggleHapticsEnabled(it) },
            onToggleSound = { viewModel.toggleSoundEnabled(it) },
            onSelectRollMode = { viewModel.setRollMode(it) },
            onDismiss = { showSettingsSheet = false }
        )
    }
}
