package com.example.testingapplication.DashBoardScreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ── Colors ──
private val BgDark       = Color(0xFF1A2132)
private val CardLight    = Color(0xFFF0F0F0)
private val TextWhite    = Color(0xFFFFFFFF)
private val TextGray     = Color(0xFF8A94A6)
private val TextDark     = Color(0xFF1A2132)
private val AccentOrange = Color(0xFFD4870A)
private val DividerColor = Color(0xFF2A3240)

@Composable
fun AddDeviceScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        AddDeviceHeader(navController)

        HorizontalDivider(color = DividerColor, thickness = 1.dp)

        WifiRow()

        HorizontalDivider(color = DividerColor, thickness = 1.dp)

        Spacer(modifier = Modifier.height(8.dp))

        // Light card panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(CardLight)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Searching indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(AccentOrange)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text  = "Searching for devices automatically...",
                    color = TextDark.copy(alpha = 0.55f),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Spinner
            CircularProgressIndicator(
                modifier    = Modifier.size(56.dp),
                color       = AccentOrange,
                strokeWidth = 3.5.dp,
                trackColor  = Color.Transparent
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Instructions
            Text(
                text      = "Press and hold the Wi-Fi button on your Smart Feeder until you see Wi-Fi Set-up. Move your phone as close as possible to the feeder you want to add.",
                color     = TextDark.copy(alpha = 0.65f),
                fontSize  = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun AddDeviceHeader(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 4.dp, vertical = 10.dp)
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
            text       = "Add or Reconnect Device",
            color      = TextWhite,
            fontWeight = FontWeight.Bold,
            fontSize   = 16.sp,
            modifier   = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun WifiRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = "Turn on Wi-Fi",
                color      = TextWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text     = "Wi-Fi is required to search for devices",
                color    = TextGray,
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector        = Icons.Default.Wifi,
            contentDescription = "Wi-Fi",
            tint               = TextWhite,
            modifier           = Modifier.size(22.dp)
        )
    }
}
