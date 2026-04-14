package com.example.testingapplication.DashBoardScreen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testingapplication.R

// ── Colors from Figma ──
private val BgDark     = Color(0xFF1C2333)
private val CardBg     = Color(0xFF2A3240)
private val AccentBlue = Color(0xFF3D7BF5)
private val TextWhite  = Color(0xFFFFFFFF)
private val TextGray   = Color(0xFF8A94A6)
private val GreenDot   = Color(0xFF4CAF50)
private val ButtonCard = Color(0xFF2E3A4E)

@Composable
fun DashboardScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {

        // ── Header ──
        DashboardHeader()

        Spacer(Modifier.height(16.dp))

        // ── Welcome text ──
        Text(
            text       = "Welcome to Dashboard!",
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            color      = TextWhite,
            modifier   = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(16.dp))

        // ── Pet Feeder Card ──
        PetFeederCard()

        Spacer(Modifier.height(12.dp))

        // ── Dots indicator ──
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(Modifier.size(8.dp).clip(CircleShape).background(AccentBlue))
            Spacer(Modifier.width(4.dp))
            Box(Modifier.size(8.dp).clip(CircleShape).background(TextGray.copy(alpha = 0.4f)))
        }

        Spacer(Modifier.height(24.dp))

        // ── Feed Now Button ──
        FeedNowButton()

        Spacer(Modifier.height(16.dp))

        // ── History and Schedule Row ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionCard(
                label    = "History",
                imageRes = R.drawable.dogo_photo_01,   // replace with your history icon
                modifier = Modifier.weight(1f),
                onClick = {navController.navigate("history")}
            )
            ActionCard(
                label    = "Schedule",
                imageRes = R.drawable.dogo_photo_01,   // replace with your schedule icon
                modifier = Modifier.weight(1f),
                onClick  = { navController.navigate("schedule") }            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ─────────────────────────────────────────────
// HEADER
// ─────────────────────────────────────────────

@Composable
fun DashboardHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 52.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(AccentBlue),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter            = painterResource(id = R.drawable.dogo_photo_01),
                contentDescription = "Profile",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.width(12.dp))

        // Hello text
        Column(modifier = Modifier.weight(1f)) {
            Text("Hello,",  fontSize = 13.sp, color = TextGray)
            Text("Allen",   fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }

        // Icons
        IconButton(onClick = {}) {
            Icon(Icons.Filled.Email, contentDescription = "Mail", tint = TextGray, modifier = Modifier.size(22.dp))
        }
        IconButton(onClick = {}) {
            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = TextGray, modifier = Modifier.size(22.dp))
        }
    }
}

// ─────────────────────────────────────────────
// PET FEEDER CARD
// ─────────────────────────────────────────────

@Composable
fun PetFeederCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF2E5BE8), Color(0xFF3D7BF5))
                )
            )
    ) {
        // Text content
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
        ) {
            Text(
                "PetFeederpro",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Status: ", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                Text(
                    "Connected",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = GreenDot
                )
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(GreenDot)
                )
            }
        }

        // Dog image on the right
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Image(
                painter            = painterResource(id = R.drawable.dogo_photo_01),
                contentDescription = "Pet",
                contentScale       = ContentScale.Crop,
                modifier           = Modifier.fillMaxSize().clip(CircleShape)
            )
        }

        // Decorative circles
        Box(Modifier.size(140.dp).offset(x = 160.dp, y = (-40).dp).clip(CircleShape).background(Color.White.copy(0.05f)))
        Box(Modifier.size(80.dp).offset(x = 200.dp, y = 60.dp).clip(CircleShape).background(Color.White.copy(0.05f)))
    }
}

// ─────────────────────────────────────────────
// FEED NOW BUTTON
// ─────────────────────────────────────────────

@Composable
fun FeedNowButton() {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(140.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = ButtonCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Bowl icon placeholder (replace with your actual bowl image)
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFFFB74D), Color(0xFFE65100))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.dogo_photo_01),
                    contentDescription = "Feed",
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize().clip(CircleShape)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "FEED NOW",
                fontSize   = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = TextWhite,
                letterSpacing = 1.sp
            )
        }
    }
}

// ─────────────────────────────────────────────
// ACTION CARDS (History / Schedule)
// ─────────────────────────────────────────────

@Composable
fun ActionCard(label: String, imageRes: Int, modifier: Modifier = Modifier, onClick:  () -> Unit = {} ) {
    Card(
        modifier  = modifier.height(140.dp)
            .clickable(onClick = onClick),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = ButtonCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)

    ) {
        Column(
            modifier            = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3A4558)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(id = imageRes),
                    contentDescription = label,
                    contentScale       = ContentScale.Crop,
                    modifier           = Modifier.fillMaxSize().clip(CircleShape)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                label,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold,
                color      = TextWhite
            )
        }
    }
}