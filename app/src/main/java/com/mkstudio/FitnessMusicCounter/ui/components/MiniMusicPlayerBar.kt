package com.mkstudio.FitnessMusicCounter.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkstudio.FitnessMusicCounter.ui.theme.CyanGlow
import com.mkstudio.FitnessMusicCounter.ui.theme.NeonCyan
import com.mkstudio.FitnessMusicCounter.ui.theme.SurfaceDark
import com.mkstudio.FitnessMusicCounter.ui.theme.TextMuted
import com.mkstudio.FitnessMusicCounter.ui.theme.TextWhite

@Composable
fun MiniMusicPlayerBar(
    stationName: String,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onBarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark.copy(alpha = 0.95f))
            .border(1.dp, if (isPlaying) NeonCyan.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .clickable { onBarClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) CyanGlow else Color.White.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Radio,
                        contentDescription = "Radio",
                        tint = if (isPlaying) NeonCyan else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = if (stationName.isNotEmpty()) stationName else "Select Radio",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    if (isPlaying) {
                        AudioVisualizerWave(modifier = Modifier.height(14.dp))
                    } else {
                        Text(
                            text = "Tap to choose station",
                            fontSize = 12.sp,
                            color = TextMuted,
                            maxLines = 1
                        )
                    }
                }
            }

            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isPlaying) NeonCyan else Color.White.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = if (isPlaying) Color.Black else TextWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
fun AudioVisualizerWave(
    modifier: Modifier = Modifier,
    barCount: Int = 12
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_wave")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.5.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(barCount) { index ->
            val animatedHeight by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 350 + (index * 70) % 400,
                        easing = FastOutSlowInEasing
                    ),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height((14 * animatedHeight).dp.coerceAtLeast(3.dp))
                    .clip(RoundedCornerShape(1.5.dp))
                    .background(NeonCyan)
            )
        }
    }
}
