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
import com.google.firebase.database.*

// ── Data model matching Firebase structure ──
data class NotificationItem(
    val icon: ImageVector,
    val iconTint: Color,
    val message: String,
    val highlight: String? = null,
    val highlightColor: Color = Color.Green,
    val statusIcon: ImageVector? = null,
    val statusTint: Color = Color.Green,
    val showDots: Boolean = false
)

// ── Firebase raw model ──
data class FirebaseNotification(
    val type: String = "",
    val message: String = "",
    val highlight: String = "",
    val status: String = ""   // "success", "error", "warning", "info"
)

// ── Map Firebase data → UI model ──
fun FirebaseNotification.toNotificationItem(): NotificationItem {
    return when (type) {
        "wifi" -> NotificationItem(
            icon         = Icons.Default.Wifi,
            iconTint     = Color(0xFF4CAF50),
            message      = message,
            highlight    = highlight.ifEmpty { null },
            highlightColor = Color(0xFF4CAF50),
            statusIcon   = Icons.Default.CheckCircle,
            statusTint   = Color(0xFF4CAF50)
        )
        "feeding" -> NotificationItem(
            icon       = Icons.Default.Notifications,
            iconTint   = Color.Gray,
            message    = message,
            statusIcon = if (status == "success") Icons.Default.CheckCircle else Icons.Default.Cancel,
            statusTint = if (status == "success") Color(0xFF4CAF50) else Color(0xFFFF5722)
        )
        "food_level" -> NotificationItem(
            icon           = Icons.Default.Warning,
            iconTint       = Color(0xFFFF5722),
            message        = message,
            highlight      = highlight.ifEmpty { null },
            highlightColor = Color(0xFFFF5722),
            statusIcon     = Icons.Default.Cancel,
            statusTint     = Color(0xFFFF5722)
        )
        "schedule" -> NotificationItem(
            icon      = Icons.Default.CalendarMonth,
            iconTint  = Color.Gray,
            message   = message,
            showDots  = true
        )
        "device" -> NotificationItem(
            icon           = Icons.Default.DevicesOther,
            iconTint       = Color(0xFF4CAF50),
            message        = message,
            highlight      = highlight.ifEmpty { null },
            highlightColor = Color(0xFF4CAF50)
        )
        else -> NotificationItem(
            icon     = Icons.Default.Notifications,
            iconTint = Color.Gray,
            message  = message
        )
    }
}

@Composable
fun NotificationScreen(navController: NavController) {
    // ── State ──
    var notifications by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }
    var isLoading     by remember { mutableStateOf(true) }
    var errorMessage  by remember { mutableStateOf<String?>(null) }

    // ── Firebase listener ──
    val ref = remember {
        FirebaseDatabase.getInstance().getReference("petfeeder/notifications")
    }

    LaunchedEffect(Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fetched = mutableListOf<NotificationItem>()

                for (child in snapshot.children) {
                    val type      = child.child("type").getValue(String::class.java) ?: ""
                    val message   = child.child("message").getValue(String::class.java) ?: ""
                    val highlight = child.child("highlight").getValue(String::class.java) ?: ""
                    val status    = child.child("status").getValue(String::class.java) ?: ""

                    val raw = FirebaseNotification(type, message, highlight, status)
                    fetched.add(raw.toNotificationItem())
                }

                notifications = fetched
                isLoading     = false
                errorMessage  = null
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
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        NotificationHeader(navController)
        Spacer(modifier = Modifier.height(8.dp))

        when {
            // ── Loading state ──
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF3D7BF5))
                }
            }

            // ── Error state ──
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "Failed to load: $errorMessage",
                        color = Color(0xFFFF5722),
                        fontSize = 13.sp
                    )
                }
            }

            // ── Empty state ──
            notifications.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "No notifications yet.",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }

            // ── Loaded state ──
            else -> {
                NotificationList(notifications)
            }
        }
    }
}

@Composable
fun NotificationHeader(navController: NavController) {
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
                tint               = MaterialTheme.colorScheme.onPrimary
            )
        }
        Text(
            text       = "Notification",
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onPrimary,
            fontSize   = 16.sp,
            modifier   = Modifier.align(Alignment.Center)
        )
        IconButton(
            onClick  = { /* handle */ },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector        = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint               = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun NotificationList(items: List<NotificationItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            NotificationCard(item)
        }
    }
}

@Composable
fun NotificationCard(item: NotificationItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E2330))
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = item.icon,
            contentDescription = null,
            tint               = item.iconTint,
            modifier           = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))

        if (item.highlight != null) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White, fontSize = 13.sp)) {
                        append(item.message)
                    }
                    withStyle(SpanStyle(color = item.highlightColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)) {
                        append(item.highlight)
                    }
                },
                modifier = Modifier.weight(1f)
            )
        } else {
            Text(
                text     = item.message,
                color    = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
        }

        if (item.showDots) {
            Text(text = "···", color = Color.Gray, fontSize = 16.sp)
        } else if (item.statusIcon != null) {
            Icon(
                imageVector        = item.statusIcon,
                contentDescription = null,
                tint               = item.statusTint,
                modifier           = Modifier.size(20.dp)
            )
        }
    }
}