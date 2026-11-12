# ShakeRPG: Polyhedral Dice Roller

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20%28M3%29-4285F4.svg)](https://developer.android.com/jetpack/compose)
[![Room Database](https://img.shields.io/badge/Persistence-Room%20%2B%20SQLite-orange.svg)](https://developer.android.com/training/data-storage/room)
[![Google AI Studio](https://img.shields.io/badge/Study-Google%20AI%20Studio-purple.svg)](https://aistudio.google.com)

A modern, tactile Tabletop RPG companion Android application designed for tabletop gamers (D&D, Pathfinder, Call of Cthulhu). ShakeRPG combines physical device accelerometer shake detection, polyhedral dice pool creation, dynamic algebraic modifiers, custom Canvas polyhedral facet rendering, tactile haptic feedback, and on-device Room persistence.

Developed as an applied engineering study exploring rapid development and hardware-integrated Android architectures with **Google AI Studio**.

---

## Key Features

- **Full RPG Polyhedral Set**: Support for standard polyhedral dice: `d4` (Tetrahedron), `d6` (Cube), `d8` (Octahedron), `d10` (Kite), `d12` (Dodecahedron), `d20` (Icosahedron), and `d100` (Percentile).
- **Physical Shake-to-Roll**: Utilizes the hardware `SensorManager` (`Sensor.TYPE_ACCELEROMETER`) measuring net acceleration magnitude excluding gravity ($\sqrt{x^2+y^2+z^2} - g$), featuring configurable sensitivity thresholds and debounce cooldowns.
- **Strict Sensor Lifecycle Safety**: Safely attaches sensor listeners on `ON_RESUME` and unregisters on `ON_PAUSE` / `onDispose` to ensure zero background battery drain.
- **Custom Canvas Facet Visuals**: Precise geometric facet layouts and gradient depth rendering for each die type on Jetpack Compose `Canvas`.
- **D20 Critical Callouts**: Visual fanfare for **Natural 20s** (golden aura shimmer and Critical Hit banner) and **Natural 1s** (crimson flame aura and Critical Fumble banner).
- **Advanced Dice Pool Mechanics**: Advantage, Disadvantage, and Drop Lowest evaluation modes with dynamic visual strike-through on dropped dice.
- **Tactile Haptics & Procedural Audio**: Dual-phase feedback with tumbling vibration pulses, settle confirmation thuds, and in-memory synthesized acoustic wooden/acrylic dice sounds.
- **Local Persistence via Room**: Complete local SQLite persistence for roll chronicles with reactive `Flow` observations, individual roll deletion, and whole-room clearing with confirmation dialogs.
- **RPG Presets**: 1-tap quick actions for common spells and weapon attacks (*Standard d20*, *Advantage*, *Disadvantage*, *Greatsword*, *Fireball*, *Stat Roll*, *Healing Potion*, etc.).

---

## Technical Stack & Architecture

- **Language**: 100% Kotlin
- **UI Toolkit**: Jetpack Compose, Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Sensors**: Android `SensorManager` (`TYPE_ACCELEROMETER`)
- **Persistence**: Android Jetpack Room Database (`RoomDao`, `RoomDatabase`, KSP)
- **Reactive Streams**: Kotlin Coroutines & `StateFlow` / `SharedFlow`
- **Haptics & Audio**: `VibrationEffect` / `VibratorManager` & procedural `AudioTrack` PCM generation
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`), Version Catalog (`libs.versions.toml`), KSP

---

## Project Structure

```text
com.danielvilha.shakerpg/
├── MainActivity.kt               # Edge-to-edge entry point, sensor lifecycle wiring
├── audio/                        # Procedural sound synthesis & haptic feedback managers
├── data/
│   ├── local/                    # Room Entity, DAO, and AppDatabase
│   └── repository/               # RollRepository abstracting persistence
├── model/                        # RpgDieType, DieInstance, RollRecord, RollPresets
├── sensor/                       # Accelerometer ShakeDetector math & debounce
├── ui/
│   ├── DiceScreen.kt             # Primary screen layout & sheet coordinators
│   ├── components/               # Polyhedral Canvas, Arena, Selector, Breakdown, Banners
│   └── theme/                    # Material 3 dark obsidian fantasy theme & jewel-tones
└── viewmodel/                    # DiceViewModel, DiceUiState, and RollEvents
```

---

## About the Study

This project was developed by [Daniel Vilha](https://github.com/danielvilha) as part of a technical exploration into prompt-driven architecture, Android hardware sensor integration, and modern Compose state workflows using **Google AI Studio**.
