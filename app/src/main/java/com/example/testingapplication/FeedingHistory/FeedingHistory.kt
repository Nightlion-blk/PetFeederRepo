package com.example.testingapplication.FeedingHistory

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testingapplication.DB_URL
import com.google.firebase.database.*
import java.util.Date

private val BgDark    = Color(0xFF1C2333)
private val CardBg    = Color(0xFF2A3240)
private val TextWhite = Color(0xFFFFFFFF)
private val TextGray  = Color(0xFF8A94A6)

enum class HistoryStatus { Completed, Late, Missed }

data class ScheduledMealHistory(
    val status: HistoryStatus,
    val date:   Date,
    val weight: Double
)

@Composable
fun FeedingHistory(navController: NavController) {
    var historyList by remember { mutableStateOf<List<ScheduledMealHistory>>(emptyList()) }
    var isLoading   by remember { mutableStateOf(true) }
    var errorMsg    by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // ✅ Fixed — correct Asia database URL
        val ref = FirebaseDatabase.getInstance(DB_URL)
            .getReference("petfeeder/feedLog")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries = mutableListOf<ScheduledMealHistory>()
                for (child in snapshot.children) {
                    val timestampRaw = child.child("timestamp").value
                    val date: Date = when (timestampRaw) {
                        is String -> {
                            try {
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                                    .parse(timestampRaw) ?: Date()
                            } catch (e: Exception) {
                                Date()
                            }
                        }
                        is Long -> Date(timestampRaw)
                        is Int  -> Date(timestampRaw.toLong())
                        else    -> Date()
                    }

                    val source = child.child("source").getValue(String::class.java) ?: "manual"

                    entries.add(
                        ScheduledMealHistory(
                            status = HistoryStatus.Completed,
                            date   = date,
                            weight = 0.0
                        )
                    )
                }
                historyList = entries.sortedByDescending { it.date }
                isLoading   = false
            }

            override fun onCancelled(error: DatabaseError) {
                errorMsg  = error.message
                isLoading = false
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
    ) {
        FeedingHistoryHeader(navController)

        Spacer(Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF3D7BF5))
                }
            }

            errorMsg.isNotEmpty() -> {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $errorMsg", color = Color(0xFFFF5252), fontSize = 14.sp)
                }
            }

            historyList.isEmpty() -> {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No feeding history yet.", color = TextGray, fontSize = 14.sp)
                }
            }

            else -> {
                historyList.forEach { history ->
                    HistoryCard(history = history)
                }
            }
        }

        Spacer(Modifier.height(32.dp))
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
            modifier         = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint               = Color(0xFFFFFFFF),
                    modifier           = Modifier.size(22.dp)
                )
            }
        }
        Text(
            text       = "Feeding History",
            fontWeight = FontWeight.Bold,
            color      = Color(0xFFFFFFFF),
            fontSize   = 16.sp,
            modifier   = Modifier.align(Alignment.Center)
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

    val formattedDate = SimpleDateFormat(
        "dd MMM yyyy, hh:mm a",
        java.util.Locale.getDefault()
    ).format(history.date)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = statusIcon, fontSize = 24.sp)

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = formattedDate,
                color      = Color(0xFFFFFFFF),
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = if (history.weight > 0) "${history.weight}g" else "Manual feed",
                color    = TextGray,
                fontSize = 12.sp
            )
        }

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