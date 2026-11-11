package com.danielvilha.shakerpg.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danielvilha.shakerpg.model.DieInstance
import com.danielvilha.shakerpg.ui.theme.CritFailRed
import com.danielvilha.shakerpg.ui.theme.CritSuccessGold
import com.danielvilha.shakerpg.ui.theme.TextPrimary

@Composable
fun DieView(
    die: DieInstance,
    modifier: Modifier = Modifier,
    size: Dp = 82.dp
) {
    // Dynamic tumbling rotation
    val animatedRotation by animateFloatAsState(
        targetValue = die.rotationAngle,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "die_rotation"
    )

    // Scale bounce during roll
    val animatedScale by animateFloatAsState(
        targetValue = if (die.isRolling) 1.14f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "die_scale"
    )

    // Shimmer glow pulse for Critical 20
    val infiniteTransition = rememberInfiniteTransition(label = "crit_pulse")
    val critGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "crit_glow_alpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(animatedScale)
            .rotate(animatedRotation)
            .alpha(if (die.isDropped) 0.38f else 1.0f)
            .testTag("die_token_${die.dieType.label}_${die.id}"),
        contentAlignment = Alignment.Center
    ) {
        // Glowing aura underlay for Critical Hits or Fails
        if (die.isCritSuccess && !die.isRolling) {
            Box(
                modifier = Modifier
                    .size(size * 1.15f)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                CritSuccessGold.copy(alpha = critGlowAlpha),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        } else if (die.isCritFail && !die.isRolling) {
            Box(
                modifier = Modifier
                    .size(size * 1.15f)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                CritFailRed.copy(alpha = 0.7f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }

        // Custom Polyhedral Shape Canvas
        Canvas(modifier = Modifier.size(size)) {
            drawPolyhedralDie(
                dieType = die.dieType,
                primaryColor = die.dieType.primaryColor,
                secondaryColor = die.dieType.secondaryColor,
                isCritSuccess = die.isCritSuccess && !die.isRolling,
                isCritFail = die.isCritFail && !die.isRolling
            )
        }

        // Die Label tag (e.g. "d20", "d6") on top
        Text(
            text = die.dieType.label.uppercase(),
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 12.dp)
        )

        // The Die Value Centered
        val textColor = when {
            die.isCritSuccess && !die.isRolling -> CritSuccessGold
            die.isCritFail && !die.isRolling -> CritFailRed
            else -> TextPrimary
        }

        val fontSize = when {
            die.currentValue >= 100 -> 18.sp
            die.currentValue >= 10 -> 22.sp
            else -> 24.sp
        }

        Text(
            text = die.currentValue.toString(),
            color = textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 4.dp)
                .shadow(
                    elevation = if (die.isCritSuccess) 8.dp else 2.dp,
                    shape = CircleShape,
                    spotColor = if (die.isCritSuccess) CritSuccessGold else Color.Black
                )
        )

        // Dropped indicator badge
        if (die.isDropped) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.8f), shape = RoundedCornerShape(4.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "DROPPED",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
