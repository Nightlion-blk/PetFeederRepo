package com.example.testingapplication.DashBoardScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun NotificationScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())  // ✅ now uses the real Compose extension
    ) {
        NotificationHeader(navController)
        Spacer(modifier = Modifier.height(8.dp))
        NotificationList()
    }
}

// ✅ The stub below has been deleted — it was shadowing Compose's real verticalScroll

@Composable
fun NotificationHeader(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 10.dp)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = "Notification",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        IconButton(
            onClick = { /* handle */ },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

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

@Composable
fun NotificationList() {
    val items = listOf(
        NotificationItem(
            icon = Icons.Default.CalendarMonth,
            iconTint = Color.Gray,
            message = "Schedule Updated",
            showDots = true
        ),
        NotificationItem(
            icon = Icons.Default.Wifi,
            iconTint = Color(0xFF4CAF50),
            message = "WiFi ",
            highlight = "Connected",
            highlightColor = Color(0xFF4CAF50),
            statusIcon = Icons.Default.CheckCircle,
            statusTint = Color(0xFF4CAF50)
        ),
        NotificationItem(
            icon = Icons.Default.Notifications,
            iconTint = Color.Gray,
            message = "08:00 PM - Feeding Completed",
            statusIcon = Icons.Default.CheckCircle,
            statusTint = Color(0xFF4CAF50)
        ),
        NotificationItem(
            icon = Icons.Default.Warning,
            iconTint = Color(0xFFFF5722),
            message = "05:50 PM - Food Level ",
            highlight = "Low !",
            highlightColor = Color(0xFFFF5722),
            statusIcon = Icons.Default.Cancel,
            statusTint = Color(0xFFFF5722)
        ),
        NotificationItem(
            icon = Icons.Default.Notifications,
            iconTint = Color.Gray,
            message = "10:00 PM - Feeding Completed",
            statusIcon = Icons.Default.CheckCircle,
            statusTint = Color(0xFF4CAF50)
        ),
        NotificationItem(
            icon = Icons.Default.DevicesOther,
            iconTint = Color(0xFF4CAF50),
            message = "Device Connected Successfully",
            highlight = "Device Connected Successfully",
            highlightColor = Color(0xFF4CAF50)
        )
    )

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
            imageVector = item.icon,
            contentDescription = null,
            tint = item.iconTint,
            modifier = Modifier.size(20.dp)
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
                text = item.message,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
        }

        if (item.showDots) {
            Text(
                text = "···",
                color = Color.Gray,
                fontSize = 16.sp
            )
        } else if (item.statusIcon != null) {
            Icon(
                imageVector = item.statusIcon,
                contentDescription = null,
                tint = item.statusTint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}