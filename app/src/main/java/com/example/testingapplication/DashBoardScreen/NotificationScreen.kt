package com.example.testingapplication.DashBoardScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testingapplication.DB_URL
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

private val BgDark     = Color(0xFF1C2333)
private val CardBg     = Color(0xFF1E2330)
private val TextWhite  = Color(0xFFFFFFFF)
private val TextGray   = Color(0xFF8A94A6)
private val AccentBlue = Color(0xFF3D7BF5)

data class NotificationItem(
    val icon:           ImageVector,
    val iconTint:       Color,
    val message:        String,
    val highlight:      String?      = null,
    val highlightColor: Color        = Color(0xFF4CAF50),
    val statusIcon:     ImageVector? = null,
    val statusTint:     Color        = Color(0xFF4CAF50),
    val showDots:       Boolean      = false,
    val timestamp:      Long         = 0L,
    val firebaseKey:    String       = ""
)

data class FirebaseNotification(
    val type:      String = "",
    val message:   String = "",
    val highlight: String = "",
    val status:    String = "",
    val timestamp: Long   = 0L,
    val key:       String = ""
)

fun FirebaseNotification.toNotificationItem(): NotificationItem {
    return when (type) {
        "wifi" -> NotificationItem(
            icon           = Icons.Default.Wifi,
            iconTint       = Color(0xFF4CAF50),
            message        = message,
            highlight      = highlight.ifEmpty { null },
            highlightColor = Color(0xFF4CAF50),
            statusIcon     = Icons.Default.CheckCircle,
            statusTint     = Color(0xFF4CAF50),
            timestamp      = timestamp,
            firebaseKey    = key
        )
        "feeding" -> NotificationItem(
            icon        = Icons.Default.Notifications,
            iconTint    = Color.Gray,
            message     = message,
            statusIcon  = if (status == "success") Icons.Default.CheckCircle
            else Icons.Default.Cancel,
            statusTint  = if (status == "success") Color(0xFF4CAF50)
            else Color(0xFFFF5722),
            timestamp   = timestamp,
            firebaseKey = key
        )
        "food_level" -> NotificationItem(
            icon           = Icons.Default.Warning,
            iconTint       = Color(0xFFFF5722),
            message        = message,
            highlight      = highlight.ifEmpty { null },
            highlightColor = Color(0xFFFF5722),
            statusIcon     = Icons.Default.Cancel,
            statusTint     = Color(0xFFFF5722),
            timestamp      = timestamp,
            firebaseKey    = key
        )
        "schedule" -> NotificationItem(
            icon        = Icons.Default.CalendarMonth,
            iconTint    = Color.Gray,
            message     = message,
            showDots    = true,
            timestamp   = timestamp,
            firebaseKey = key
        )
        "device" -> NotificationItem(
            icon           = Icons.Default.DevicesOther,
            iconTint       = Color(0xFF4CAF50),
            message        = message,
            highlight      = highlight.ifEmpty { null },
            highlightColor = Color(0xFF4CAF50),
            timestamp      = timestamp,
            firebaseKey    = key
        )
        else -> NotificationItem(
            icon        = Icons.Default.Notifications,
            iconTint    = Color.Gray,
            message     = message,
            timestamp   = timestamp,
            firebaseKey = key
        )
    }
}

@Composable
fun NotificationScreen(navController: NavController) {
    var notifications by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }
    var isLoading     by remember { mutableStateOf(true) }
    var errorMessage  by remember { mutableStateOf<String?>(null) }

    // ✅ Fixed — correct Asia database URL
    val ref = remember {
        FirebaseDatabase.getInstance(DB_URL)
            .getReference("petfeeder/notifications")
    }

    LaunchedEffect(Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fetched = mutableListOf<FirebaseNotification>()
                for (child in snapshot.children) {
                    val type      = child.child("type").getValue(String::class.java)      ?: ""
                    val message   = child.child("message").getValue(String::class.java)   ?: ""
                    val highlight = child.child("highlight").getValue(String::class.java) ?: ""
                    val status    = child.child("status").getValue(String::class.java)    ?: ""
                    val timestamp = child.child("timestamp").getValue(Long::class.java)   ?: 0L
                    val key       = child.key ?: ""
                    fetched.add(FirebaseNotification(type, message, highlight, status, timestamp, key))
                }
                notifications = fetched
                    .sortedByDescending { it.timestamp }
                    .map { it.toNotificationItem() }
                isLoading    = false
                errorMessage = null
            }

            override fun onCancelled(error: DatabaseError) {
                errorMessage = error.message
                isLoading    = false
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
    ) {
        NotificationHeader(
            navController = navController,
            hasItems      = notifications.isNotEmpty(),
            onClearAll    = { ref.removeValue() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentBlue)
                }
            }

            errorMessage != null -> {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text     = "Failed to load: $errorMessage",
                        color    = Color(0xFFFF5722),
                        fontSize = 13.sp
                    )
                }
            }

            notifications.isEmpty() -> {
                Box(
                    modifier         = Modifier.fillMaxWidth().padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint               = TextGray,
                            modifier           = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text     = "No notifications yet.",
                            color    = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            else -> {
                NotificationList(items = notifications, ref = ref)
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun NotificationHeader(
    navController: NavController,
    hasItems:      Boolean,
    onClearAll:    () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 10.dp)
    ) {
        IconButton(
            onClick  = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector        = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint               = TextWhite
            )
        }

        Text(
            text       = "Notifications",
            fontWeight = FontWeight.Bold,
            color      = TextWhite,
            fontSize   = 16.sp,
            modifier   = Modifier.align(Alignment.Center)
        )

        if (hasItems) {
            TextButton(
                onClick  = onClearAll,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(text = "Clear all", color = AccentBlue, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun NotificationList(
    items: List<NotificationItem>,
    ref:   DatabaseReference
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            NotificationCard(
                item     = item,
                onDelete = {
                    if (item.firebaseKey.isNotEmpty()) {
                        ref.child(item.firebaseKey).removeValue()
                    }
                }
            )
        }
    }
}

@Composable
fun NotificationCard(
    item:     NotificationItem,
    onDelete: () -> Unit
) {
    val timeText = remember(item.timestamp) {
        if (item.timestamp > 0) {
            SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
                .format(Date(item.timestamp))
        } else ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = item.icon,
            contentDescription = null,
            tint               = item.iconTint,
            modifier           = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            if (item.highlight != null) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = TextWhite, fontSize = 13.sp)) {
                            append(item.message)
                        }
                        withStyle(
                            SpanStyle(
                                color      = item.highlightColor,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(item.highlight)
                        }
                    }
                )
            } else {
                Text(text = item.message, color = TextWhite, fontSize = 13.sp)
            }

            if (timeText.isNotEmpty()) {
                Spacer(Modifier.height(3.dp))
                Text(text = timeText, color = TextGray, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (item.showDots) {
            Text(text = "···", color = TextGray, fontSize = 16.sp)
        } else if (item.statusIcon != null) {
            Icon(
                imageVector        = item.statusIcon,
                contentDescription = null,
                tint               = item.statusTint,
                modifier           = Modifier.size(20.dp)
            )
        }

        IconButton(
            onClick  = onDelete,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.Close,
                contentDescription = "Delete",
                tint               = TextGray,
                modifier           = Modifier.size(14.dp)
            )
        }
    }
}