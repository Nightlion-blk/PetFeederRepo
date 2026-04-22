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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// ─── Color Palette ────────────────────────────────────────────────────────────
private val BackgroundColor  = Color(0xFF0F1117)
private val CardColor        = Color(0xFF1A1D27)
private val SectionLabel     = Color(0xFF6B7280)
private val TextPrimary      = Color(0xFFE5E7EB)
private val TextSecondary    = Color(0xFF9CA3AF)
private val AccentBlue       = Color(0xFF3B82F6)

// ─── Data Models ──────────────────────────────────────────────────────────────

/** Represents a single row in the settings list. */
sealed class SettingRow {
    /** A row with a trailing arrow (navigation) */
    data class Arrow(
        val icon: ImageVector,
        val iconBackground: Color,
        val title: String,
        val subtitle: String
    ) : SettingRow()

    /** A row with a toggle switch */
    data class Toggle(
        val icon: ImageVector,
        val iconBackground: Color,
        val title: String,
        val subtitle: String,
        val isEnabled: Boolean
    ) : SettingRow()
}

/** A section groups related rows under a label. */
data class SettingsSection(
    val label: String,
    val rows: List<SettingRow>
)


@Composable
fun SettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        SettingsHeader(navController)
        Spacer(modifier = Modifier.height(8.dp))
        SettingsList()
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─── Header ───────────────────────────────────────────────────────────────────

@Composable
fun SettingsHeader(navController: NavController) {
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
                tint = TextPrimary
            )
        }

        Text(
            text = "Settings",
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
@Composable
fun SettingsList() {
    // Mutable toggle states
    var darkModeEnabled       by remember { mutableStateOf(false) }
    var locationAccessEnabled by remember { mutableStateOf(true)  }
    var notificationsEnabled  by remember { mutableStateOf(true)  }

    val sections = listOf(
        SettingsSection(
            label = "Personalisation",
            rows  = listOf(
                SettingRow.Arrow(
                    icon            = Icons.Default.Language,
                    iconBackground  = Color(0xFF1E3A5F),
                    title           = "Time Zone",
                    subtitle        = "Choose your timezone"
                ),
                SettingRow.Arrow(
                    icon            = Icons.Default.Translate,
                    iconBackground  = Color(0xFF3B1F5E),
                    title           = "Language",
                    subtitle        = "Set the app language"
                ),
                SettingRow.Toggle(
                    icon            = Icons.Default.DarkMode,
                    iconBackground  = Color(0xFF5E3A1A),
                    title           = "Dark mode",
                    subtitle        = "Choose view mode",
                    isEnabled       = darkModeEnabled
                )
            )
        ),
        SettingsSection(
            label = "Access",
            rows  = listOf(
                SettingRow.Toggle(
                    icon            = Icons.Default.LocationOn,
                    iconBackground  = Color(0xFF1A4A2E),
                    title           = "Location access",
                    subtitle        = "Access to your location",
                    isEnabled       = locationAccessEnabled
                )
            )
        ),
        SettingsSection(
            label = "Notifications",
            rows  = listOf(
                SettingRow.Toggle(
                    icon            = Icons.Default.Notifications,
                    iconBackground  = Color(0xFF4A3A1A),
                    title           = "App Notifications",
                    subtitle        = "Get push notifications",
                    isEnabled       = notificationsEnabled
                )
            )
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        sections.forEach { section ->
            SettingsSectionBlock(
                section         = section,
                darkModeEnabled = darkModeEnabled,
                locationEnabled = locationAccessEnabled,
                notifEnabled    = notificationsEnabled,
                onDarkModeToggle       = { darkModeEnabled = it },
                onLocationToggle       = { locationAccessEnabled = it },
                onNotificationsToggle  = { notificationsEnabled = it }
            )
        }
    }
}

// ─── Section Block ────────────────────────────────────────────────────────────

@Composable
fun SettingsSectionBlock(
    section: SettingsSection,
    darkModeEnabled: Boolean,
    locationEnabled: Boolean,
    notifEnabled: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onLocationToggle: (Boolean) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Section label
        Text(
            text       = section.label,
            color      = TextPrimary,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        // Cards
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            section.rows.forEach { row ->
                when (row) {
                    is SettingRow.Arrow  -> ArrowSettingCard(row)
                    is SettingRow.Toggle -> {
                        // Resolve live state per row title
                        val checked = when (row.title) {
                            "Dark mode"           -> darkModeEnabled
                            "Location access"     -> locationEnabled
                            "App Notifications"   -> notifEnabled
                            else                  -> row.isEnabled
                        }
                        val onChecked: (Boolean) -> Unit = when (row.title) {
                            "Dark mode"           -> onDarkModeToggle
                            "Location access"     -> onLocationToggle
                            "App Notifications"   -> onNotificationsToggle
                            else                  -> { _ -> }
                        }
                        ToggleSettingCard(row = row, checked = checked, onCheckedChange = onChecked)
                    }
                }
            }
        }
    }
}

// ─── Arrow Card ───────────────────────────────────────────────────────────────

@Composable
fun ArrowSettingCard(row: SettingRow.Arrow) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardColor)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon = row.icon, background = row.iconBackground)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = row.title,    color = TextPrimary,   fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(text = row.subtitle, color = TextSecondary, fontSize = 12.sp)
        }
        Icon(
            imageVector     = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint            = AccentBlue,
            modifier        = Modifier.size(20.dp)
        )
    }
}

// ─── Toggle Card ──────────────────────────────────────────────────────────────

@Composable
fun ToggleSettingCard(
    row: SettingRow.Toggle,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardColor)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingIcon(icon = row.icon, background = row.iconBackground)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = row.title,    color = TextPrimary,   fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Text(text = row.subtitle, color = TextSecondary, fontSize = 12.sp)
        }
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors          = SwitchDefaults.colors(
                checkedThumbColor        = Color.White,
                checkedTrackColor        = AccentBlue,
                uncheckedThumbColor      = Color.White,
                uncheckedTrackColor      = Color(0xFF374151),
                uncheckedBorderColor     = Color(0xFF374151)
            )
        )
    }
}

// ─── Icon Helper ──────────────────────────────────────────────────────────────

@Composable
fun SettingIcon(icon: ImageVector, background: Color) {
    Box(
        modifier        = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector     = icon,
            contentDescription = null,
            tint            = Color.White,
            modifier        = Modifier.size(22.dp)
        )
    }
}