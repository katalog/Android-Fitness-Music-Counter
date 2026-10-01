package com.mkstudio.FitnessMusicCounter.ui.views.count

import android.app.Activity
import android.view.WindowManager
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkstudio.FitnessMusicCounter.model.TabataPhase
import com.mkstudio.FitnessMusicCounter.model.WorkoutConfig
import com.mkstudio.FitnessMusicCounter.model.WorkoutMode
import com.mkstudio.FitnessMusicCounter.ui.components.CircularProgressGauge
import com.mkstudio.FitnessMusicCounter.ui.components.MiniMusicPlayerBar
import com.mkstudio.FitnessMusicCounter.ui.components.RadioStationSheet
import com.mkstudio.FitnessMusicCounter.ui.theme.BgDark
import com.mkstudio.FitnessMusicCounter.ui.theme.ElectricVolt
import com.mkstudio.FitnessMusicCounter.ui.theme.FlameCoral
import com.mkstudio.FitnessMusicCounter.ui.theme.NeonCyan
import com.mkstudio.FitnessMusicCounter.ui.theme.SurfaceDark
import com.mkstudio.FitnessMusicCounter.ui.theme.SurfaceVariantDark
import com.mkstudio.FitnessMusicCounter.ui.theme.TextMuted
import com.mkstudio.FitnessMusicCounter.ui.theme.TextWhite
import com.mkstudio.FitnessMusicCounter.ui.viewmodel.CountViewModel
import com.mkstudio.FitnessMusicCounter.ui.viewmodel.MainViewModel

@Composable
fun WorkoutScreen(
    countViewModel: CountViewModel,
    mainViewModel: MainViewModel,
    workoutConfig: WorkoutConfig,
    onFinishWorkout: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Keep screen on during workout
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        countViewModel.initWorkout(workoutConfig)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            countViewModel.stopAllTimers()
        }
    }

    val workoutTime by countViewModel.workoutTime.collectAsState()
    val repsCnt by countViewModel.repsCnt.collectAsState()
    val laps by countViewModel.laps.collectAsState()

    // Mode-specific states
    val isResting by countViewModel.isResting.collectAsState()
    val restSeconds by countViewModel.restSecondsRemaining.collectAsState()

    val tabataPhase by countViewModel.tabataPhase.collectAsState()
    val phaseSeconds by countViewModel.phaseSecondsRemaining.collectAsState()

    val currentStation by mainViewModel.stationName.collectAsState()
    val isPlaying by mainViewModel.isPlaying.collectAsState()

    var showCompleteDialog by remember { mutableStateOf(false) }
    var showStationSheet by remember { mutableStateOf(false) }

    val lapListState = rememberLazyListState()

    LaunchedEffect(laps.size) {
        if (laps.isNotEmpty()) {
            lapListState.animateScrollToItem(laps.size - 1)
        }
    }

    LaunchedEffect(Unit) {
        countViewModel.isCompleted.collect {
            showCompleteDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = workoutConfig.mode.title.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (workoutConfig.mode) {
                            WorkoutMode.TABATA -> FlameCoral
                            WorkoutMode.REST_TIMER -> NeonCyan
                            WorkoutMode.MANUAL -> ElectricVolt
                        },
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Fitness Session",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                }

                IconButton(
                    onClick = {
                        countViewModel.saveToDatabase()
                        onFinishWorkout()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark)
                        .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Finish Workout",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Center Display based on Mode
            when (workoutConfig.mode) {
                WorkoutMode.TABATA -> {
                    // TABATA UI
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .clip(CircleShape)
                                .background(SurfaceDark)
                                .border(
                                    width = 12.dp,
                                    color = when (tabataPhase) {
                                        TabataPhase.PREPARE -> Color.Yellow
                                        TabataPhase.WORK -> ElectricVolt
                                        TabataPhase.REST -> FlameCoral
                                        TabataPhase.FINISHED -> NeonCyan
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = when (tabataPhase) {
                                        TabataPhase.PREPARE -> "PREPARE"
                                        TabataPhase.WORK -> "🔥 WORK!"
                                        TabataPhase.REST -> "🧘 REST"
                                        TabataPhase.FINISHED -> "DONE!"
                                    },
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = when (tabataPhase) {
                                        TabataPhase.PREPARE -> Color.Yellow
                                        TabataPhase.WORK -> ElectricVolt
                                        TabataPhase.REST -> FlameCoral
                                        TabataPhase.FINISHED -> NeonCyan
                                    }
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format("%02d", phaseSeconds),
                                    fontSize = 68.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextWhite,
                                    lineHeight = 70.sp
                                )
                                Text(
                                    text = "SET $repsCnt / ${workoutConfig.targetSets}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                WorkoutMode.REST_TIMER -> {
                    // REST TIMER UI
                    if (isResting) {
                        // Resting View (Big Countdown)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(240.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceDark)
                                    .border(10.dp, FlameCoral, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "REST TIME",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FlameCoral,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = String.format("%02d:%02d", restSeconds / 60, restSeconds % 60),
                                        fontSize = 58.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace,
                                        color = TextWhite
                                    )
                                    Text(
                                        text = "Next: Set ${repsCnt + 1} / ${workoutConfig.targetSets}",
                                        fontSize = 13.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Button(
                                    onClick = { countViewModel.addRestTime(15) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark)
                                ) {
                                    Text("+15s", color = TextWhite)
                                }
                                Button(
                                    onClick = { countViewModel.skipRest() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ElectricVolt,
                                        contentColor = BgDark
                                    )
                                ) {
                                    Icon(Icons.Default.FastForward, contentDescription = null, tint = BgDark)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Skip Rest", color = BgDark, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // Working Set View
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressGauge(
                                currentSets = repsCnt,
                                targetSets = workoutConfig.targetSets,
                                size = 220.dp,
                                strokeWidth = 14.dp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    countViewModel.completeSetAndStartRest()
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(60.dp)
                                    .clip(RoundedCornerShape(30.dp)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricVolt,
                                    contentColor = BgDark
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = BgDark
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "COMPLETE SET ${repsCnt + 1}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp,
                                        color = BgDark
                                    )
                                }
                            }
                        }
                    }
                }

                WorkoutMode.MANUAL -> {
                    // MANUAL UI (+ / - buttons)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressGauge(
                            currentSets = repsCnt,
                            targetSets = workoutConfig.targetSets,
                            size = 220.dp,
                            strokeWidth = 14.dp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceDark)
                                    .border(2.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        countViewModel.removeManualSet()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Minus", tint = TextWhite, modifier = Modifier.size(32.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .size(86.dp)
                                    .clip(CircleShape)
                                    .background(SurfaceDark)
                                    .border(3.dp, ElectricVolt, CircleShape)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        countViewModel.addManualSet()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = ElectricVolt, modifier = Modifier.size(42.dp))
                            }
                        }
                    }
                }
            }

            // Stopwatch Timer Display
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = workoutTime,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = TextWhite,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Pulse",
                    tint = FlameCoral,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Lap Times Horizontal Row
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "COMPLETED SETS (${laps.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                if (laps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceDark)
                            .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sets will be recorded here",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                } else {
                    LazyRow(
                        state = lapListState,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(laps) { lap ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceDark)
                                    .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Set ${lap.setNumber}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = lap.durationText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricVolt
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Floating Mini Radio Player Bar
            MiniMusicPlayerBar(
                stationName = currentStation,
                isPlaying = isPlaying,
                onTogglePlay = { mainViewModel.toggleRadioPlay() },
                onBarClick = { showStationSheet = true }
            )
        }
    }

    if (showStationSheet) {
        RadioStationSheet(
            currentStation = currentStation,
            onSelectStation = { mainViewModel.playRadio(it) },
            onDismiss = { showStationSheet = false }
        )
    }

    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = { showCompleteDialog = false },
            title = {
                Text(
                    text = "Workout Complete! 🎉",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Text(
                    text = "Finished [${workoutConfig.mode.title}] with ${workoutConfig.targetSets} sets in $workoutTime. Session saved to history!",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCompleteDialog = false
                        onFinishWorkout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricVolt)
                ) {
                    Text("View History", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showCompleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark)
                ) {
                    Text("Stay Here", color = TextWhite)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
