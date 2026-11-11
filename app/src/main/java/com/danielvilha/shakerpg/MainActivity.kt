package com.danielvilha.shakerpg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.danielvilha.shakerpg.audio.DiceAudioManager
import com.danielvilha.shakerpg.audio.DiceHapticsManager
import com.danielvilha.shakerpg.sensor.ShakeDetector
import com.danielvilha.shakerpg.ui.DiceScreen
import com.danielvilha.shakerpg.ui.theme.ObsidianBackground
import com.danielvilha.shakerpg.ui.theme.ShakeRPGTheme
import com.danielvilha.shakerpg.data.local.AppDatabase
import com.danielvilha.shakerpg.data.repository.RollRepository
import com.danielvilha.shakerpg.viewmodel.DiceRollEvent
import com.danielvilha.shakerpg.viewmodel.DiceViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: DiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ShakeRPGTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ObsidianBackground
                ) {
                    ShakeRPGApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun ShakeRPGApp(viewModel: DiceViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Initialize Room Database repository
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { RollRepository(database.rollDao()) }

    LaunchedEffect(repository) {
        viewModel.setRepository(repository)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val hapticsManager = remember { DiceHapticsManager(context) }
    val audioManager = remember { DiceAudioManager() }

    // Sync settings with audio and haptics managers
    LaunchedEffect(uiState.isHapticsEnabled) {
        hapticsManager.isEnabled = uiState.isHapticsEnabled
    }
    LaunchedEffect(uiState.isSoundEnabled) {
        audioManager.isEnabled = uiState.isSoundEnabled
    }

    // Initialize accelerometer ShakeDetector
    val shakeDetector = remember {
        ShakeDetector(context) {
            viewModel.rollDice()
        }
    }

    // Sync shake sensitivity and enabled state
    LaunchedEffect(uiState.shakeSensitivity, uiState.isShakeEnabled) {
        shakeDetector.sensitivity = uiState.shakeSensitivity
        shakeDetector.isEnabled = uiState.isShakeEnabled
        if (!uiState.isShakeEnabled) {
            shakeDetector.stopListening()
        } else {
            shakeDetector.startListening()
        }
    }

    // Lifecycle safety: Register on RESUME, unregister on PAUSE / dispose
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (uiState.isShakeEnabled) {
                        shakeDetector.startListening()
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    shakeDetector.stopListening()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            shakeDetector.stopListening()
        }
    }

    // Handle audio & haptic roll events
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is DiceRollEvent.TumbleTick -> {
                    hapticsManager.playTumbleTick()
                    audioManager.playTumbleClick()
                }
                is DiceRollEvent.Settle -> {
                    hapticsManager.playSettleFeedback(event.isCritSuccess, event.isCritFail)
                    audioManager.playSettleSound(event.isCritSuccess)
                }
                is DiceRollEvent.ButtonTap -> {
                    hapticsManager.playButtonTap()
                }
            }
        }
    }

    val shakeIntensity by shakeDetector.currentShakeIntensity.collectAsStateWithLifecycle()

    DiceScreen(
        viewModel = viewModel,
        uiState = uiState,
        shakeIntensity = shakeIntensity
    )
}
