package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.data.CardSurfaceShader

/**
 * Renders shader and texture overlays over physical credit/debit card faces.
 */
@Composable
fun CardTextureShaderOverlay(
    shader: CardSurfaceShader,
    baseColor: Color,
    modifier: Modifier = Modifier
) {
    if (shader == CardSurfaceShader.CLASSIC_GRADIENT) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        when (shader) {
            CardSurfaceShader.CLASSIC_GRADIENT -> {
                // Handled by primary gradient background
            }

            CardSurfaceShader.BRUSHED_TITANIUM -> {
                // Horizontal fine-line brushed metallic streaks
                val lineCount = 40
                val stepY = height / lineCount
                for (i in 0..lineCount) {
                    val y = i * stepY
                    val alpha = if (i % 2 == 0) 0.08f else 0.04f
                    drawLine(
                        color = Color.White.copy(alpha = alpha),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.2f
                    )
                }
                // Sheen spotlight
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.Transparent,
                            Color.White.copy(alpha = 0.12f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(width, height)
                    )
                )
            }

            CardSurfaceShader.HOLOGRAPHIC_FOIL -> {
                // Diagonal rainbow iridescent specular sheen
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFF0080).copy(alpha = 0.16f), // Pink
                            Color(0xFF7928CA).copy(alpha = 0.14f), // Purple
                            Color(0xFF0070F3).copy(alpha = 0.18f), // Blue
                            Color(0xFF00DFD8).copy(alpha = 0.16f), // Cyan
                            Color(0xFFFFEE00).copy(alpha = 0.14f), // Yellow
                            Color(0xFFFF0080).copy(alpha = 0.12f)
                        ),
                        start = Offset(0f, height * 0.2f),
                        end = Offset(width * 1.2f, height * 0.8f)
                    )
                )
            }

            CardSurfaceShader.FROSTED_GLASS -> {
                // Subtle frosted glass glare & corner glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.28f),
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(width * 0.15f, height * 0.2f),
                        radius = width * 0.45f
                    )
                )
                // Border glow line
                drawRect(
                    color = Color.White.copy(alpha = 0.08f)
                )
            }

            CardSurfaceShader.CARBON_FIBER -> {
                // Tactical micro diamond grid weave
                val step = 14f
                var x = 0f
                while (x < width) {
                    drawLine(
                        color = Color.Black.copy(alpha = 0.25f),
                        start = Offset(x, 0f),
                        end = Offset(x + height, height),
                        strokeWidth = 1.2f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.06f),
                        start = Offset(x + 2f, 0f),
                        end = Offset(x + height + 2f, height),
                        strokeWidth = 1f
                    )
                    x += step
                }
                var x2 = -height
                while (x2 < width) {
                    drawLine(
                        color = Color.Black.copy(alpha = 0.22f),
                        start = Offset(x2 + height, 0f),
                        end = Offset(x2, height),
                        strokeWidth = 1.2f
                    )
                    x2 += step
                }
            }

            CardSurfaceShader.GUILLOCHE -> {
                // Intricate undulating security curve wave (banknote style)
                val path = Path()
                val waveCount = 5
                for (w in 0 until waveCount) {
                    val offsetY = height * (0.25f + w * 0.12f)
                    path.reset()
                    path.moveTo(0f, offsetY)
                    var curX = 0f
                    val waveLen = width / 6f
                    while (curX < width) {
                        path.cubicTo(
                            curX + waveLen * 0.25f, offsetY - 14f,
                            curX + waveLen * 0.75f, offsetY + 14f,
                            curX + waveLen, offsetY
                        )
                        curX += waveLen
                    }
                    drawPath(
                        path = path,
                        color = Color.White.copy(alpha = 0.12f),
                        style = Stroke(width = 1.4f)
                    )
                }
            }
        }
    }
}
