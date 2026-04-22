package com.example.testingapplication.DashBoardScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
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
import com.example.testingapplication.DB_URL
import com.example.testingapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private val BgDark     = Color(0xFF1C2333)
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

        // ✅ Replaced PetFeederCard() with swipable pager
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
                imageRes = R.drawable.dogo_photo_01,
                modifier = Modifier.weight(1f),
                onClick  = { navController.navigate("history") }
            )
            ActionCard(
                label    = "Schedule",
                imageRes = R.drawable.dogo_photo_01,
                modifier = Modifier.weight(1f),
                onClick  = { navController.navigate("schedule") }
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

// ─────────────────────────────────────────────
// HEADER
// ─────────────────────────────────────────────

@Composable
fun DashboardHeader(navController: NavController) {
    val auth        = remember { FirebaseAuth.getInstance() }
    val user        = auth.currentUser
    val displayName = user?.displayName ?: "User"

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
            Text("Hello,",  fontSize = 13.sp, color = TextGray)
            Text(
                text       = displayName,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = TextWhite
            )
        }

        IconButton(onClick = { navController.navigate("notification") }) {
            Icon(
                Icons.Filled.Notifications,
                contentDescription = "Notifications",
                tint               = TextGray,
                modifier           = Modifier.size(22.dp)
            )
        }

        IconButton(onClick = { navController.navigate("settings") }) {
            Icon(
                Icons.Filled.Menu,
                contentDescription = "Menu",
                tint               = TextGray,
                modifier           = Modifier.size(22.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────
// PET FEEDER CARD PAGER (swipable)
// ─────────────────────────────────────────────

@Composable
fun PetFeederCardPager() {
    val pagerState = rememberPagerState(pageCount = { 2 })

    Column {
        HorizontalPager(
            state    = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> PetFeederCard()   // swipe left to see food level
                1 -> FoodLevelCard()   // swipe right to go back
            }
        }

        Spacer(Modifier.height(10.dp))

        // ✅ Dot indicators
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

// ─────────────────────────────────────────────
// PAGE 1 — PET FEEDER CARD
// ─────────────────────────────────────────────

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
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF2E5BE8), Color(0xFF3D7BF5))
                )
            )
    ) {
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
                    text       = if (isOnline) "Connected" else "Offline",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = if (isOnline) GreenDot else Color(0xFFFF5252)
                )
                Spacer(Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isOnline) GreenDot else Color(0xFFFF5252))
                )
            }
        }

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

        Box(
            Modifier.size(140.dp).offset(x = 160.dp, y = (-40).dp)
                .clip(CircleShape).background(Color.White.copy(0.05f))
        )
        Box(
            Modifier.size(80.dp).offset(x = 200.dp, y = 60.dp)
                .clip(CircleShape).background(Color.White.copy(0.05f))
        )
    }
}

// ─────────────────────────────────────────────
// PAGE 2 — FOOD LEVEL CARD
// ─────────────────────────────────────────────

@Composable
fun FoodLevelCard() {
    var foodLevel by remember { mutableStateOf(0) } // 0–100

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
        foodLevel >= 60 -> Color(0xFF4CAF50)   // green  — plenty
        foodLevel >= 30 -> Color(0xFFFFB74D)   // orange — getting low
        else            -> Color(0xFFFF5252)   // red    — almost empty
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(140.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFF1B3A2D), Color(0xFF2D6A4F))
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text       = "Food Level",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress   = { foodLevel / 100f },
                modifier   = Modifier
                    .fillMaxWidth(0.75f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color      = levelColor,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text     = "$foodLevel% remaining",
                fontSize = 13.sp,
                color    = Color.White.copy(alpha = 0.8f)
            )
        }

        // decorative circles
        Box(
            Modifier.size(100.dp).offset(x = 200.dp, y = (-30).dp)
                .clip(CircleShape).background(Color.White.copy(0.05f))
        )
        Box(
            Modifier.size(60.dp).offset(x = 230.dp, y = 60.dp)
                .clip(CircleShape).background(Color.White.copy(0.05f))
        )
    }
}

// ─────────────────────────────────────────────
// FEED NOW BUTTON
// ─────────────────────────────────────────────

@Composable
fun FeedNowButton() {
    val ref = remember {
        FirebaseDatabase.getInstance(DB_URL)
            .getReference("petfeeder/command")
    }
    var isFeedSent by remember { mutableStateOf(false) }

    Card(
        onClick = {
            ref.child("feed").setValue(true)
            ref.child("timestamp").setValue(System.currentTimeMillis())
            isFeedSent = true
        },
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
                text          = if (isFeedSent) "SENT! ✓" else "FEED NOW",
                fontSize      = 16.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = if (isFeedSent) Color(0xFF4CAF50) else TextWhite,
                letterSpacing = 1.sp
            )
        }
    }
}

// ─────────────────────────────────────────────
// ACTION CARDS
// ─────────────────────────────────────────────

@Composable
fun ActionCard(
    label:    String,
    imageRes: Int,
    modifier: Modifier = Modifier,
    onClick:  () -> Unit = {}
) {
    Card(
        onClick   = onClick,
        modifier  = modifier.height(140.dp),
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
                text       = label,
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold,
                color      = TextWhite
            )
        }
    }
}