package com.danielvilha.shakerpg.ui.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.danielvilha.shakerpg.model.RpgDieType
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws realistic geometric facets and polyhedral outlines for tabletop RPG dice.
 */
fun DrawScope.drawPolyhedralDie(
    dieType: RpgDieType,
    primaryColor: Color,
    secondaryColor: Color,
    isCritSuccess: Boolean,
    isCritFail: Boolean
) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f

    val strokeColor = when {
        isCritSuccess -> Color(0xFFFFD700)
        isCritFail -> Color(0xFFFF3D00)
        else -> Color.White.copy(alpha = 0.45f)
    }
    val strokeWidth = if (isCritSuccess || isCritFail) 3.5f else 1.8f

    val gradientBrush = Brush.radialGradient(
        colors = listOf(
            primaryColor,
            secondaryColor,
            Color.Black.copy(alpha = 0.6f)
        ),
        center = Offset(cx * 0.85f, cy * 0.75f),
        radius = w * 0.7f
    )

    when (dieType) {
        RpgDieType.D4 -> {
            // Triangle with central apex facets
            val path = Path().apply {
                moveTo(cx, h * 0.12f)
                lineTo(w * 0.88f, h * 0.88f)
                lineTo(w * 0.12f, h * 0.88f)
                close()
            }
            drawPath(path, brush = gradientBrush)
            drawPath(path, color = strokeColor, style = Stroke(strokeWidth))

            // Inner facet lines meeting at center
            drawLine(strokeColor, Offset(cx, h * 0.12f), Offset(cx, cy * 1.15f), strokeWidth = 1.2f)
            drawLine(strokeColor, Offset(w * 0.88f, h * 0.88f), Offset(cx, cy * 1.15f), strokeWidth = 1.2f)
            drawLine(strokeColor, Offset(w * 0.12f, h * 0.88f), Offset(cx, cy * 1.15f), strokeWidth = 1.2f)
        }

        RpgDieType.D6 -> {
            // Rounded cube face with isometric bevel
            val r = w * 0.18f
            drawRoundRect(
                brush = gradientBrush,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r)
            )
            drawRoundRect(
                color = strokeColor,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
                style = Stroke(strokeWidth)
            )
            // Subtle inner inset bevel
            val inset = w * 0.08f
            drawRoundRect(
                color = Color.White.copy(alpha = 0.15f),
                topLeft = Offset(inset, inset),
                size = androidx.compose.ui.geometry.Size(w - inset * 2, h - inset * 2),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(r * 0.7f, r * 0.7f),
                style = Stroke(1.0f)
            )
        }

        RpgDieType.D8 -> {
            // Octahedron / Diamond shape
            val path = Path().apply {
                moveTo(cx, h * 0.08f) // top
                lineTo(w * 0.92f, cy) // right
                lineTo(cx, h * 0.92f) // bottom
                lineTo(w * 0.08f, cy) // left
                close()
            }
            drawPath(path, brush = gradientBrush)
            drawPath(path, color = strokeColor, style = Stroke(strokeWidth))

            // Equator facet lines
            drawLine(strokeColor, Offset(w * 0.08f, cy), Offset(w * 0.92f, cy), strokeWidth = 1.2f)
            drawLine(strokeColor, Offset(cx, h * 0.08f), Offset(cx, h * 0.92f), strokeWidth = 1.2f)
        }

        RpgDieType.D10 -> {
            // Kite / Pentagonal trapezohedron
            val path = Path().apply {
                moveTo(cx, h * 0.06f) // Top apex
                lineTo(w * 0.92f, h * 0.42f) // Upper right
                lineTo(w * 0.72f, h * 0.92f) // Lower right
                lineTo(w * 0.28f, h * 0.92f) // Lower left
                lineTo(w * 0.08f, h * 0.42f) // Upper left
                close()
            }
            drawPath(path, brush = gradientBrush)
            drawPath(path, color = strokeColor, style = Stroke(strokeWidth))

            // Inner facet radiating to center
            val facetCenter = Offset(cx, cy * 0.9f)
            drawLine(strokeColor, Offset(cx, h * 0.06f), facetCenter, strokeWidth = 1.2f)
            drawLine(strokeColor, Offset(w * 0.92f, h * 0.42f), facetCenter, strokeWidth = 1.2f)
            drawLine(strokeColor, Offset(w * 0.72f, h * 0.92f), facetCenter, strokeWidth = 1.2f)
            drawLine(strokeColor, Offset(w * 0.28f, h * 0.92f), facetCenter, strokeWidth = 1.2f)
            drawLine(strokeColor, Offset(w * 0.08f, h * 0.42f), facetCenter, strokeWidth = 1.2f)
        }

        RpgDieType.D12 -> {
            // Dodecahedron regular pentagon with facet network
            val radius = w * 0.46f
            val path = Path()
            for (i in 0 until 5) {
                val angle = Math.toRadians((i * 72 - 90).toDouble())
                val px = cx + (radius * cos(angle)).toFloat()
                val py = cy + (radius * sin(angle)).toFloat()
                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            path.close()

            drawPath(path, brush = gradientBrush)
            drawPath(path, color = strokeColor, style = Stroke(strokeWidth))

            // Inner smaller pentagon face
            val innerR = radius * 0.52f
            val innerPath = Path()
            val innerPoints = mutableListOf<Offset>()
            for (i in 0 until 5) {
                val angle = Math.toRadians((i * 72 - 90).toDouble())
                val px = cx + (innerR * cos(angle)).toFloat()
                val py = cy + (innerR * sin(angle)).toFloat()
                innerPoints.add(Offset(px, py))
                if (i == 0) innerPath.moveTo(px, py) else innerPath.lineTo(px, py)
            }
            innerPath.close()
            drawPath(innerPath, color = strokeColor, style = Stroke(1.2f))

            // Connect outer vertices to inner vertices
            for (i in 0 until 5) {
                val outerAngle = Math.toRadians((i * 72 - 90).toDouble())
                val ox = cx + (radius * cos(outerAngle)).toFloat()
                val oy = cy + (radius * sin(outerAngle)).toFloat()
                drawLine(strokeColor, Offset(ox, oy), innerPoints[i], strokeWidth = 1.0f)
            }
        }

        RpgDieType.D20 -> {
            // 20-sided icosahedron: Outer regular hexagon with central equilateral triangle
            val radius = w * 0.47f
            val path = Path()
            val outerVertices = mutableListOf<Offset>()
            for (i in 0 until 6) {
                val angle = Math.toRadians((i * 60 - 30).toDouble())
                val px = cx + (radius * cos(angle)).toFloat()
                val py = cy + (radius * sin(angle)).toFloat()
                outerVertices.add(Offset(px, py))
                if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
            }
            path.close()

            drawPath(path, brush = gradientBrush)
            drawPath(path, color = strokeColor, style = Stroke(strokeWidth))

            // Central prominent triangle facet
            val triR = radius * 0.55f
            val t1 = Offset(cx, cy - triR)
            val t2 = Offset(cx + triR * 0.866f, cy + triR * 0.5f)
            val t3 = Offset(cx - triR * 0.866f, cy + triR * 0.5f)

            val centralTri = Path().apply {
                moveTo(t1.x, t1.y)
                lineTo(t2.x, t2.y)
                lineTo(t3.x, t3.y)
                close()
            }
            drawPath(centralTri, color = Color.White.copy(alpha = 0.08f))
            drawPath(centralTri, color = strokeColor, style = Stroke(1.3f))

            // Radiating facet connector lines to hexagon corners
            drawLine(strokeColor, t1, outerVertices[0], strokeWidth = 1.0f)
            drawLine(strokeColor, t1, outerVertices[5], strokeWidth = 1.0f)
            drawLine(strokeColor, t2, outerVertices[1], strokeWidth = 1.0f)
            drawLine(strokeColor, t2, outerVertices[2], strokeWidth = 1.0f)
            drawLine(strokeColor, t3, outerVertices[3], strokeWidth = 1.0f)
            drawLine(strokeColor, t3, outerVertices[4], strokeWidth = 1.0f)
        }

        RpgDieType.D100 -> {
            // Percentile die with segmented outer ring
            drawCircle(brush = gradientBrush)
            drawCircle(color = strokeColor, style = Stroke(strokeWidth))

            // Radiating tick lines
            for (i in 0 until 10) {
                val angle = Math.toRadians((i * 36).toDouble())
                val startR = w * 0.42f
                val endR = w * 0.48f
                val x1 = cx + (startR * cos(angle)).toFloat()
                val y1 = cy + (startR * sin(angle)).toFloat()
                val x2 = cx + (endR * cos(angle)).toFloat()
                val y2 = cy + (endR * sin(angle)).toFloat()
                drawLine(strokeColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = 1.5f)
            }

            // Inner circle ring
            drawCircle(
                color = strokeColor.copy(alpha = 0.4f),
                radius = w * 0.38f,
                style = Stroke(1.0f)
            )
        }
    }
}
