package com.danielvilha.shakerpg.model

import androidx.compose.ui.graphics.Color
import com.danielvilha.shakerpg.ui.theme.DieD100Teal
import com.danielvilha.shakerpg.ui.theme.DieD10Amethyst
import com.danielvilha.shakerpg.ui.theme.DieD12Bronze
import com.danielvilha.shakerpg.ui.theme.DieD20Gold
import com.danielvilha.shakerpg.ui.theme.DieD4Amber
import com.danielvilha.shakerpg.ui.theme.DieD6Emerald
import com.danielvilha.shakerpg.ui.theme.DieD8Sapphire
import kotlin.random.Random

/**
 * Standard RPG Polyhedral dice representations.
 */
enum class RpgDieType(
    val sides: Int,
    val label: String,
    val fullName: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val geometricName: String
) {
    D4(
        sides = 4,
        label = "d4",
        fullName = "Tetrahedron",
        primaryColor = DieD4Amber,
        secondaryColor = Color(0xFFBF360C),
        geometricName = "Triangle"
    ),
    D6(
        sides = 6,
        label = "d6",
        fullName = "Cube",
        primaryColor = DieD6Emerald,
        secondaryColor = Color(0xFF1B5E20),
        geometricName = "Rounded Square"
    ),
    D8(
        sides = 8,
        label = "d8",
        fullName = "Octahedron",
        primaryColor = DieD8Sapphire,
        secondaryColor = Color(0xFF0D47A1),
        geometricName = "Diamond"
    ),
    D10(
        sides = 10,
        label = "d10",
        fullName = "Pentagonal Trapezohedron",
        primaryColor = DieD10Amethyst,
        secondaryColor = Color(0xFF4A148C),
        geometricName = "Kite"
    ),
    D12(
        sides = 12,
        label = "d12",
        fullName = "Dodecahedron",
        primaryColor = DieD12Bronze,
        secondaryColor = Color(0xFFE65100),
        geometricName = "Pentagon"
    ),
    D20(
        sides = 20,
        label = "d20",
        fullName = "Icosahedron",
        primaryColor = DieD20Gold,
        secondaryColor = Color(0xFF8D6E00),
        geometricName = "Shield"
    ),
    D100(
        sides = 100,
        label = "d100",
        fullName = "Percentile Zocchihedron",
        primaryColor = DieD100Teal,
        secondaryColor = Color(0xFF006064),
        geometricName = "Percentile"
    );

    fun roll(): Int {
        return Random.nextInt(1, sides + 1)
    }
}
