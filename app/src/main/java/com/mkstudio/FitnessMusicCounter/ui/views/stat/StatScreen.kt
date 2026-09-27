package com.mkstudio.FitnessMusicCounter.ui.views.stat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkstudio.FitnessMusicCounter.repo.db.WorkoutRecord
import com.mkstudio.FitnessMusicCounter.ui.theme.BgDark
import com.mkstudio.FitnessMusicCounter.ui.theme.ElectricVolt
import com.mkstudio.FitnessMusicCounter.ui.theme.FlameCoral
import com.mkstudio.FitnessMusicCounter.ui.theme.NeonCyan
import com.mkstudio.FitnessMusicCounter.ui.theme.SurfaceDark
import com.mkstudio.FitnessMusicCounter.ui.theme.SurfaceVariantDark
import com.mkstudio.FitnessMusicCounter.ui.theme.TextMuted
import com.mkstudio.FitnessMusicCounter.ui.theme.TextWhite
import com.mkstudio.FitnessMusicCounter.ui.viewmodel.MainViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val records by viewModel.records.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(SurfaceDark)
                    .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "STATISTICS & HISTORY",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricVolt,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Past Workouts",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = TextWhite
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (records.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Workout Records Yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Complete your first workout to see analytics",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(records, key = { it.id }) { record ->
                    WorkoutRecordCard(
                        record = record,
                        onDelete = { viewModel.deleteRecord(record) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkoutRecordCard(
    record: WorkoutRecord,
    onDelete: () -> Unit
) {
    val sets = record.setstime.split(";").filter { it.isNotBlank() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = record.date,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${sets.size} Sets Completed",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElectricVolt
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Record",
                        tint = FlameCoral.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (sets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    sets.forEach { setInfo ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceVariantDark)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = setInfo,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
