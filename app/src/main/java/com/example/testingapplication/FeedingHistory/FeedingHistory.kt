package com.example.testingapplication.FeedingHistory

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.navigation.NavController
import java.util.Date

private val BgDark     = Color(0xFF1C2333)
private val CardBg     = Color(0xFF2A3240)
private val AccentBlue = Color(0xFF3D7BF5)
private val TextWhite  = Color(0xFFFFFFFF)
private val TextGray   = Color(0xFF8A94A6)
private val GreenDot   = Color(0xFF4CAF50)
private val ButtonCard = Color(0xFF2E3A4E)

enum class HistoryStatus{
    Completed,
    Late,
    Missed,
}

data class ScheduledMealHistory(
    val status: HistoryStatus,
    val date: Date,
    val weight: Double
)

@Composable
fun FeedingHistory(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
    ) {
        FeedingHistoryHeader(navController)
        HistoryCard(
            history = ScheduledMealHistory(
                status = HistoryStatus.Completed,
                date   = Date(),
                weight = 100.0
            )
        )
    }
}

@Composable
fun FeedingHistoryHeader(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Text(
            text = "Feeding History",
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
@Composable
fun HistoryCard(history: ScheduledMealHistory) {
    val statusColor = when (history.status) {
        HistoryStatus.Completed -> Color(0xFF4CAF50)
        HistoryStatus.Late      -> Color(0xFFFFC107)
        HistoryStatus.Missed    -> Color(0xFFE53935)
    }

    val statusLabel = when (history.status) {
        HistoryStatus.Completed -> "Completed"
        HistoryStatus.Late      -> "Late"
        HistoryStatus.Missed    -> "Missed"
    }

    val statusIcon = when (history.status) {
        HistoryStatus.Completed -> "✅"
        HistoryStatus.Late      -> "⚠️"
        HistoryStatus.Missed    -> "❌"
    }

    val formattedDate = SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
        .format(history.date)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Status icon
        Text(text = statusIcon, fontSize = 24.sp)

        Spacer(modifier = Modifier.width(12.dp))

        // Date and weight
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = formattedDate,
                color      = TextWhite,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = "${history.weight}g",
                color    = TextGray,
                fontSize = 12.sp
            )
        }

        // Status badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(statusColor.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text       = statusLabel,
                color      = statusColor,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}