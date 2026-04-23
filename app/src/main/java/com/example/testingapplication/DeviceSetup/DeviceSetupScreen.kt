package com.example.testingapplication.DeviceSetup

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import androidx.navigation.NavController

private val BgDark     = Color(0xFF1C2333)
private val AccentBlue = Color(0xFF3D7BF5)
private val TextWhite  = Color(0xFFFFFFFF)
private val TextGray   = Color(0xFF8A94A6)
private val GreenDot   = Color(0xFF4CAF50)
private val RedDot     = Color(0xFFFF5252)
private val ButtonCard = Color(0xFF2E3A4E)

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun DeviceSetupScreen(navController: NavController, viewModel: ProvisioningViewModel) {
    val state by viewModel.state.collectAsState()
    var ssid     by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 52.dp, start = 8.dp, end = 20.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back",
                    tint = TextGray, modifier = Modifier.size(22.dp))
            }
            Text("Connect Device", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            Text(
                text     = "Connect your PetFeederpro to your home WiFi in a few simple steps.",
                fontSize = 13.sp,
                color    = TextGray,
                modifier = Modifier.padding(bottom = 24.dp),
                lineHeight = 20.sp
            )

            // ── Steps
            SetupStep(
                number      = 1,
                title       = "Power on your device",
                description = "It will broadcast \"MyDevice-Setup\" hotspot",
                isActive    = true,
                isDone      = state is ProvisioningState.SendingCredentials
                        || state is ProvisioningState.WaitingForDeviceOnline
                        || state is ProvisioningState.Success
            )
            SetupStep(
                number      = 2,
                title       = "Send WiFi credentials",
                description = "The app connects to your device and sends credentials",
                isActive    = state is ProvisioningState.SendingCredentials
                        || state is ProvisioningState.WaitingForDeviceOnline
                        || state is ProvisioningState.Success,
                isDone      = state is ProvisioningState.WaitingForDeviceOnline
                        || state is ProvisioningState.Success
            )
            SetupStep(
                number      = 3,
                title       = "Device goes online",
                description = "Confirmed via Firebase when connected",
                isActive    = state is ProvisioningState.WaitingForDeviceOnline
                        || state is ProvisioningState.Success,
                isDone      = state is ProvisioningState.Success
            )

            Spacer(Modifier.height(8.dp))

            // ── Input fields (only show when Idle or Error)
            AnimatedVisibility(visible = state is ProvisioningState.Idle || state is ProvisioningState.Error) {
                Column {
                    Text("WIFI NAME (SSID)", fontSize = 11.sp, color = TextGray,
                        fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value         = ssid,
                        onValueChange = { ssid = it },
                        placeholder   = { Text("e.g. HomeNetwork_5G", color = TextGray, fontSize = 14.sp) },
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = AccentBlue,
                            unfocusedBorderColor = Color.White.copy(0.08f),
                            focusedTextColor     = TextWhite,
                            unfocusedTextColor   = TextWhite,
                            cursorColor          = AccentBlue,
                            focusedContainerColor   = ButtonCard,
                            unfocusedContainerColor = ButtonCard
                        )
                    )
                    Spacer(Modifier.height(14.dp))
                    Text("WIFI PASSWORD", fontSize = 11.sp, color = TextGray,
                        fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value               = password,
                        onValueChange       = { password = it },
                        placeholder         = { Text("••••••••", color = TextGray, fontSize = 14.sp) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier            = Modifier.fillMaxWidth(),
                        shape               = RoundedCornerShape(12.dp),
                        colors              = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = AccentBlue,
                            unfocusedBorderColor = Color.White.copy(0.08f),
                            focusedTextColor     = TextWhite,
                            unfocusedTextColor   = TextWhite,
                            cursorColor          = AccentBlue,
                            focusedContainerColor   = ButtonCard,
                            unfocusedContainerColor = ButtonCard
                        )
                    )
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick  = { viewModel.startProvisioning(ssid, password) },
                        enabled  = ssid.isNotBlank(),
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(14.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = AccentBlue,
                            disabledContainerColor = AccentBlue.copy(0.4f)
                        )
                    ) {
                        Text("Connect Device", fontSize = 15.sp, fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp)
                    }
                }
            }

            // ── Status feedback
            Spacer(Modifier.height(16.dp))
            AnimatedVisibility(visible = state !is ProvisioningState.Idle) {
                StatusCard(state = state, onRetry = { viewModel.reset() })
            }
        }
    }
}

// ── STEP INDICATOR ───────────────────────────────

@Composable
fun SetupStep(
    number:      Int,
    title:       String,
    description: String,
    isActive:    Boolean,
    isDone:      Boolean
) {
    Row(
        modifier = Modifier.padding(bottom = 20.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone   -> GreenDot
                        isActive -> AccentBlue
                        else     -> ButtonCard
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(Icons.Filled.Check, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(14.dp))
            } else {
                Text(
                    text     = "$number",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color    = if (isActive) Color.White else TextGray
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.padding(top = 2.dp)) {
            Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                color = if (isActive || isDone) TextWhite else TextGray)
            Text(description, fontSize = 12.sp, color = TextGray,
                modifier = Modifier.padding(top = 2.dp))
        }
    }
}

// ── STATUS CARD ──────────────────────────────────

@Composable
fun StatusCard(state: ProvisioningState, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = ButtonCard)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (state) {
                is ProvisioningState.Success -> {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null,
                        tint = GreenDot, modifier = Modifier.size(20.dp))
                    Text("Device connected successfully!", fontSize = 13.sp, color = GreenDot,
                        modifier = Modifier.weight(1f))
                }
                is ProvisioningState.Error -> {
                    Icon(Icons.Filled.Error, contentDescription = null,
                        tint = RedDot, modifier = Modifier.size(20.dp))
                    Text(state.message, fontSize = 13.sp, color = TextWhite,
                        modifier = Modifier.weight(1f))
                    TextButton(onClick = onRetry) {
                        Text("Retry", color = AccentBlue, fontSize = 13.sp)
                    }
                }
                else -> {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(18.dp),
                        color     = AccentBlue,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = when (state) {
                            is ProvisioningState.ConnectingToDevice   -> "Connecting to device hotspot..."
                            is ProvisioningState.SendingCredentials   -> "Sending WiFi credentials..."
                            is ProvisioningState.WaitingForDeviceOnline -> "Waiting for device to come online..."
                            else -> ""
                        },
                        fontSize = 13.sp,
                        color    = TextWhite,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}