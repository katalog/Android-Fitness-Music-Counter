package com.mkstudio.FitnessMusicCounter.ui.views.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkstudio.FitnessMusicCounter.model.WorkoutConfig
import com.mkstudio.FitnessMusicCounter.model.WorkoutMode
import com.mkstudio.FitnessMusicCounter.ui.components.RadioStationSheet
import com.mkstudio.FitnessMusicCounter.ui.components.stationInfoList
import com.mkstudio.FitnessMusicCounter.ui.theme.BgDark
import com.mkstudio.FitnessMusicCounter.ui.theme.CyanGlow
import com.mkstudio.FitnessMusicCounter.ui.theme.ElectricVolt
import com.mkstudio.FitnessMusicCounter.ui.theme.NeonCyan
import com.mkstudio.FitnessMusicCounter.ui.theme.SurfaceDark
import com.mkstudio.FitnessMusicCounter.ui.theme.SurfaceVariantDark
import com.mkstudio.FitnessMusicCounter.ui.theme.TextMuted
import com.mkstudio.FitnessMusicCounter.ui.theme.TextWhite
import com.mkstudio.FitnessMusicCounter.ui.viewmodel.MainViewModel

@Composable
fun SetupScreen(
    viewModel: MainViewModel,
    onStartWorkout: (WorkoutConfig) -> Unit,
    onNavigateToStats: () -> Unit
) {
    val savedTargetSets by viewModel.repsMax.collectAsState()
    val currentStation by viewModel.stationName.collectAsState()

    var selectedMode by remember { mutableStateOf(WorkoutMode.REST_TIMER) }
    var targetSets by remember(savedTargetSets) { mutableIntStateOf(if (savedTargetSets > 0) savedTargetSets else 10) }
    var restDuration by remember { mutableIntStateOf(60) }
    var workDuration by remember { mutableIntStateOf(20) }

    var showStationSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WORKOUT SETUP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricVolt,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Session Mode",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextWhite
                )
            }

            IconButton(
                onClick = onNavigateToStats,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Workout History",
                    tint = TextWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3-Mode Selector Tabs
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "SELECT WORKOUT MODE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                WorkoutMode.values().forEach { mode ->
                    val isSelected = mode == selectedMode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) ElectricVolt else Color.Transparent)
                            .clickable {
                                selectedMode = mode
                                if (mode == WorkoutMode.TABATA && targetSets > 15) {
                                    targetSets = 8
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = when (mode) {
                                    WorkoutMode.TABATA -> "1. 타바타"
                                    WorkoutMode.REST_TIMER -> "2. 자동 휴식"
                                    WorkoutMode.MANUAL -> "3. 수동"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.Black else TextWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = selectedMode.subtitle,
                fontSize = 12.sp,
                color = NeonCyan,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mode-Specific Settings Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceDark)
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Target Sets Stepper
                Text(
                    text = "Target Sets",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { if (targetSets > 1) targetSets-- },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SurfaceVariantDark)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextWhite)
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Text(
                        text = String.format("%02d", targetSets),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricVolt,
                        lineHeight = 54.sp
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    IconButton(
                        onClick = { if (targetSets < 50) targetSets++ },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SurfaceVariantDark)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextWhite)
                    }
                }

                Slider(
                    value = targetSets.toFloat(),
                    onValueChange = { targetSets = it.toInt() },
                    valueRange = 1f..30f,
                    steps = 28,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricVolt,
                        activeTrackColor = ElectricVolt,
                        inactiveTrackColor = SurfaceVariantDark
                    ),
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                // Additional Controls based on mode
                when (selectedMode) {
                    WorkoutMode.TABATA -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Work duration
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfaceVariantDark)
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("운동 시간 (Work)", fontSize = 12.sp, color = TextMuted)
                                Text("${workDuration}초", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ElectricVolt)
                                Row {
                                    Text("20s", modifier = Modifier.clickable { workDuration = 20 }.padding(4.dp), color = if (workDuration == 20) ElectricVolt else TextMuted, fontSize = 12.sp)
                                    Text("30s", modifier = Modifier.clickable { workDuration = 30 }.padding(4.dp), color = if (workDuration == 30) ElectricVolt else TextMuted, fontSize = 12.sp)
                                    Text("45s", modifier = Modifier.clickable { workDuration = 45 }.padding(4.dp), color = if (workDuration == 45) ElectricVolt else TextMuted, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Rest duration
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfaceVariantDark)
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("휴식 시간 (Rest)", fontSize = 12.sp, color = TextMuted)
                                Text("${restDuration}초", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                                Row {
                                    Text("10s", modifier = Modifier.clickable { restDuration = 10 }.padding(4.dp), color = if (restDuration == 10) NeonCyan else TextMuted, fontSize = 12.sp)
                                    Text("15s", modifier = Modifier.clickable { restDuration = 15 }.padding(4.dp), color = if (restDuration == 15) NeonCyan else TextMuted, fontSize = 12.sp)
                                    Text("20s", modifier = Modifier.clickable { restDuration = 20 }.padding(4.dp), color = if (restDuration == 20) NeonCyan else TextMuted, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    WorkoutMode.REST_TIMER -> {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceVariantDark)
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("세트 간 자동 휴식 시간", fontSize = 13.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${restDuration}초 (${restDuration / 60}분 ${if (restDuration % 60 > 0) "${restDuration % 60}초" else ""})",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(30, 60, 90, 120, 180).forEach { sec ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (restDuration == sec) NeonCyan else Color.White.copy(alpha = 0.05f))
                                            .clickable { restDuration = sec }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "${sec}s",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (restDuration == sec) Color.Black else TextWhite
                                        )
                                    }
                                }
                            }
                        }
                    }

                    WorkoutMode.MANUAL -> {
                        // Manual explanation
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "세트 완료 시 + 버튼을 눌러 개별 랩타임을 기록합니다.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Choose Station Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Choose Radio Station",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Text(
                text = "See All",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = NeonCyan,
                modifier = Modifier.clickable { showStationSheet = true }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Station Cards Carousel
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(stationInfoList) { info ->
                val isSelected = info.name == currentStation

                Box(
                    modifier = Modifier
                        .width(145.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) SurfaceVariantDark else SurfaceDark)
                        .border(
                            1.5.dp,
                            if (isSelected) NeonCyan else Color.White.copy(alpha = 0.05f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { viewModel.playRadio(info.name) }
                        .padding(12.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) CyanGlow else Color.White.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radio,
                                contentDescription = null,
                                tint = if (isSelected) NeonCyan else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = info.name.replace("_", " "),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) NeonCyan else TextWhite,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = info.bpm,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ElectricVolt
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Start Workout Button
        Button(
            onClick = {
                viewModel.setRepsMax(targetSets)
                val config = WorkoutConfig(
                    mode = selectedMode,
                    targetSets = targetSets,
                    restDurationSeconds = restDuration,
                    workDurationSeconds = workDuration
                )
                onStartWorkout(config)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(30.dp)),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricVolt,
                contentColor = Color.Black
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "START WORKOUT",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showStationSheet) {
        RadioStationSheet(
            currentStation = currentStation,
            onSelectStation = { viewModel.playRadio(it) },
            onDismiss = { showStationSheet = false }
        )
    }
}
