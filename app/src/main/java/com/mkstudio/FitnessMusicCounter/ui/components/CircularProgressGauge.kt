package com.mkstudio.FitnessMusicCounter.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkstudio.FitnessMusicCounter.ui.theme.CyanGlow
import com.mkstudio.FitnessMusicCounter.ui.theme.ElectricVolt
import com.mkstudio.FitnessMusicCounter.ui.theme.NeonCyan
import com.mkstudio.FitnessMusicCounter.ui.theme.SurfaceVariantDark
import com.mkstudio.FitnessMusicCounter.ui.theme.TextMuted
import com.mkstudio.FitnessMusicCounter.ui.theme.TextWhite

@Composable
fun CircularProgressGauge(
    currentSets: Int,
    targetSets: Int,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    strokeWidth: Dp = 14.dp
) {
    val progress = if (targetSets > 0) (currentSets.toFloat() / targetSets.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600),
        label = "gauge_progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()

            // Background Track
            drawArc(
                color = SurfaceVariantDark,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Outer Glow Layer
            if (animatedProgress > 0f) {
                drawArc(
                    color = CyanGlow,
                    startAngle = 135f,
                    sweepAngle = 270f * animatedProgress,
                    useCenter = false,
                    style = Stroke(width = strokePx * 1.8f, cap = StrokeCap.Round)
                )
            }

            // Foreground Progress Arc with Gradient
            val gradientBrush = Brush.sweepGradient(
                listOf(NeonCyan, ElectricVolt, NeonCyan)
            )
            drawArc(
                brush = gradientBrush,
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = String.format("%02d", currentSets),
                fontSize = 56.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite,
                lineHeight = 58.sp
            )
            Text(
                text = "/ ${String.format("%02d", targetSets)} SETS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (currentSets >= targetSets && targetSets > 0) "COMPLETED!" else "Current Set",
                fontSize = 12.sp,
                color = if (currentSets >= targetSets && targetSets > 0) ElectricVolt else TextMuted,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
