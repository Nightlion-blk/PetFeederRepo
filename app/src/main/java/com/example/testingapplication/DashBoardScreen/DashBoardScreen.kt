package com.example.testingapplication.DashBoardScreen

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.example.testingapplication.DB_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

private val BgDark     = Color(0xFF1C2333)
private val AccentBlue = Color(0xFF3D7BF5)
private val AccentBlue2= Color(0xFF2E5BE8)
private val TextWhite  = Color(0xFFFFFFFF)
private val TextGray   = Color(0xFF8A94A6)
private val GreenDot   = Color(0xFF4CAF50)
private val RedDot     = Color(0xFFFF5252)
private val ButtonCard = Color(0xFF2E3A4E)
private val DarkCard   = Color(0xFF3A4558)

@Composable
fun DashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
    ) {
        DashboardHeader(navController)
        Spacer(Modifier.height(16.dp))
        Text(
            text       = "Welcome to Dashboard!",
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            color      = TextWhite,
            modifier   = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(Modifier.height(16.dp))
        PetFeederCardPager()
        Spacer(Modifier.height(24.dp))
        FeedNowButton()
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionCard(
                label    = "History",
                icon     = Icons.Filled.List,
                modifier = Modifier.weight(1f),
                onClick  = { navController.navigate("history") }
            )
            ActionCard(
                label    = "Device Setup",
                icon     = Icons.Filled.Wifi,
                modifier = Modifier.weight(1f),
                onClick  = { navController.navigate("devicesetup") }
            )
        }
        Spacer(Modifier.height(32.dp))
    }
}

// ── HEADER ──────────────────────────────────────

@Composable
fun DashboardHeader(navController: NavController) {
    val auth        = remember { FirebaseAuth.getInstance() }
    val displayName = auth.currentUser?.displayName ?: "User"
    var showLogout  by remember { mutableStateOf(false) }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            title  = { Text("Log Out", fontWeight = FontWeight.Bold, color = TextWhite) },
            text   = { Text("Are you sure you want to log out?", color = TextGray) },
            confirmButton = {
                TextButton(onClick = {
                    showLogout = false
                    auth.signOut()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }) {
                    Text("Log Out", color = RedDot, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogout = false }) {
                    Text("Cancel", color = TextGray)
                }
            },
            containerColor = ButtonCard
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 52.dp, start = 20.dp, end = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(AccentBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = displayName.first().uppercaseChar().toString(),
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = TextWhite
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Hello,",     fontSize = 13.sp, color = TextGray)
            Text(displayName,  fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }
        IconButton(onClick = { navController.navigate("notification") }) {
            Icon(Icons.Filled.Notifications, contentDescription = "Notifications",
                tint = TextGray, modifier = Modifier.size(22.dp))
        }
        IconButton(onClick = { showLogout = true }) {
            Icon(Icons.Filled.Menu, contentDescription = "Menu",
                tint = TextGray, modifier = Modifier.size(22.dp))
        }
    }
}

// ── PAGER ────────────────────────────────────────

@Composable
fun PetFeederCardPager() {
    val pagerState = rememberPagerState(pageCount = { 2 })

    Column {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
            when (page) {
                0 -> PetFeederCard()
                1 -> FoodLevelCard()
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(2) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (pagerState.currentPage == index) 8.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) AccentBlue
                            else TextGray.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}

// ── PAGE 1: PET FEEDER CARD ──────────────────────

@Composable
fun PetFeederCard() {
    var isOnline by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance(DB_URL)
            .getReference("petfeeder/status/online")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isOnline = snapshot.getValue(Boolean::class.java) ?: false
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(AccentBlue2, AccentBlue)))
    ) {
        // Decorative circles
        Box(Modifier.size(140.dp).offset(x = 160.dp, y = (-40).dp)
            .clip(CircleShape).background(Color.White.copy(0.05f)))
        Box(Modifier.size(80.dp).offset(x = 200.dp, y = 60.dp)
            .clip(CircleShape).background(Color.White.copy(0.05f)))

        Column(
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 20.dp)
        ) {
            Text("PetFeederpro", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Status: ", fontSize = 13.sp, color = Color.White.copy(0.8f))
                Text(
                    text       = if (isOnline) "Connected" else "Offline",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (isOnline) GreenDot else RedDot
                )
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier.size(7.dp).clip(CircleShape)
                        .background(if (isOnline) GreenDot else RedDot)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clip(CircleShape)
                .background(Color.White.copy(0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text("🐶", fontSize = 36.sp)
        }
    }
}

// ── PAGE 2: FOOD LEVEL CARD ──────────────────────

@Composable
fun FoodLevelCard() {
    var foodLevel by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance(DB_URL)
            .getReference("petfeeder/status/foodLevel")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    foodLevel = snapshot.getValue(Int::class.java) ?: 0
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    val levelColor = when {
        foodLevel >= 60 -> GreenDot
        foodLevel >= 30 -> Color(0xFFFFB74D)
        else            -> RedDot
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF1B3A2D), Color(0xFF2D6A4F))))
            .padding(20.dp)
    ) {
        Box(Modifier.size(100.dp).offset(x = 200.dp, y = (-30).dp)
            .clip(CircleShape).background(Color.White.copy(0.05f)))
        Box(Modifier.size(60.dp).offset(x = 230.dp, y = 60.dp)
            .clip(CircleShape).background(Color.White.copy(0.05f)))

        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text("Food Level", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress   = { foodLevel / 100f },
                modifier   = Modifier.fillMaxWidth(0.75f).height(10.dp).clip(RoundedCornerShape(5.dp)),
                color      = levelColor,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            Spacer(Modifier.height(8.dp))
            Text("$foodLevel% remaining", fontSize = 13.sp, color = Color.White.copy(0.8f))
        }
    }
}

// ── FEED NOW BUTTON ──────────────────────────────

@Composable
fun FeedNowButton() {
    val ref = remember {
        FirebaseDatabase.getInstance(DB_URL).getReference("petfeeder/command")
    }
    var isFeedSent by remember { mutableStateOf(false) }

    Card(
        onClick = {
            ref.child("feed").setValue(true)
            ref.child("timestamp").setValue(System.currentTimeMillis())
            isFeedSent = true
        },
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(140.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = ButtonCard),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(72.dp).clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFFFFB74D), Color(0xFFE65100)))),
                contentAlignment = Alignment.Center
            ) {
                Text("🍖", fontSize = 28.sp)
            }
            Spacer(Modifier.height(12.dp))
            AnimatedContent(targetState = isFeedSent) { sent ->
                Text(
                    text          = if (sent) "SENT! ✓" else "FEED NOW",
                    fontSize      = 16.sp,
                    fontWeight    = FontWeight.ExtraBold,
                    color         = if (sent) GreenDot else TextWhite,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

// ── ACTION CARDS ─────────────────────────────────

@Composable
fun ActionCard(
    label:    String,
    icon:     androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick:  () -> Unit = {}
) {
    Card(
        onClick   = onClick,
        modifier  = modifier.height(140.dp),
        shape     = RoundedCornerShape(20.dp),
        colors    = CardDefaults.cardColors(containerColor = ButtonCard),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(DarkCard),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = label, tint = TextWhite, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(label, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
        }
    }
}