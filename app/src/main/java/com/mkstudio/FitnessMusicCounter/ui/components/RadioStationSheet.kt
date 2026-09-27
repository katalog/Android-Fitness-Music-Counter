package com.mkstudio.FitnessMusicCounter.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkstudio.FitnessMusicCounter.ui.theme.ElectricVolt
import com.mkstudio.FitnessMusicCounter.ui.theme.NeonCyan
import com.mkstudio.FitnessMusicCounter.ui.theme.SurfaceDark
import com.mkstudio.FitnessMusicCounter.ui.theme.SurfaceVariantDark
import com.mkstudio.FitnessMusicCounter.ui.theme.TextMuted
import com.mkstudio.FitnessMusicCounter.ui.theme.TextWhite
import com.mkstudio.FitnessMusicCounter.util.Constants

data class StationInfo(
    val name: String,
    val genre: String,
    val bpm: String
)

val stationInfoList = listOf(
    StationInfo("NRJ_Fitness", "Energetic EDM / Workout", "130 BPM"),
    StationInfo("NRJ_Dance", "Club & Dance Floor", "128 BPM"),
    StationInfo("NRJ_Berlin", "Electro & Tech House", "126 BPM"),
    StationInfo("NRJ_HitRemix", "Top Hits Club Remix", "125 BPM"),
    StationInfo("NRJ_PartyHits", "Non-stop Party Hits", "122 BPM"),
    StationInfo("NRJ_Hit2000", "2000s Pop & Dance Classics", "120 BPM")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RadioStationSheet(
    currentStation: String,
    onSelectStation: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Select Radio Station",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Workout motivation with high-energy live streams",
                fontSize = 13.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                items(stationInfoList) { info ->
                    val isSelected = info.name == currentStation

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) SurfaceVariantDark else Color.White.copy(alpha = 0.03f))
                            .border(
                                width = 1.dp,
                                color = if (isSelected) NeonCyan else Color.White.copy(alpha = 0.06f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                onSelectStation(info.name)
                                onDismiss()
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = if (isSelected) NeonCyan else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = info.name.replace("_", " "),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) NeonCyan else TextWhite
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${info.genre} • ${info.bpm}",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = ElectricVolt,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
