package com.example.testingapplication.DashBoardScreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.Image          // ✅ correct Image import
import androidx.compose.ui.res.painterResource
import com.example.testingapplication.R          // ✅ your app R, not android.R

private val BgDark     = Color(0xFF1C2333)
private val CardBg     = Color(0xFF2A3240)
private val AccentBlue = Color(0xFF3D7BF5)
private val TextWhite  = Color(0xFFFFFFFF)
private val TextGray   = Color(0xFF8A94A6)

@Composable
fun FoodLevel(navController: NavController) {  // ✅ lowercase navController
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
    ) {
        FoodLevelHeader(navController)
        PetImage(null)
        Spacer(Modifier.height(16.dp))
        PercentageInfoCard(70, "2 hours ago", "8:00 PM / 10:00 AM")
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun FoodLevelHeader(navController: NavController) {  // ✅ lowercase
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .align(Alignment.CenterStart),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Text(
            text = "Food Level",
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun PetImage(petId: Int?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .background(CardBg, RoundedCornerShape(16.dp))
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            if (petId != null) {
                Image(
                    painter = painterResource(id = R.drawable.dogo_photo_01), // ✅ your drawable name
                    contentDescription = "Pet image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(Color(0xFF1C2333)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Pets,
                        contentDescription = "No pet image",
                        tint = TextGray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(32.dp)
                    .background(Color.Black.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = "Camera",
                    tint = TextWhite,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Your pet is waiting\nfor food!",
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentBlue,
                contentColor = TextWhite
            )
        ) {
            Text("Feed Now", fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun PercentageInfoCard(
    foodLevel: Int = 70,
    lastFeeding: String = "2 hours ago",
    schedule: String = "8:00 PM / 10:00 AM"
) {
    val targetProgress by remember { mutableFloatStateOf(foodLevel / 100f) }
    val animated by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(CardBg, RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF3D4E63), RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "Food Level: $foodLevel%",
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { animated },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = AccentBlue,
                trackColor = BgDark
            )
        }

        HorizontalDivider(color = Color(0xFF3D4E63), thickness = 0.5.dp)

        Row {
            Text("Last Feeding: ", color = TextWhite, fontSize = 14.sp)
            Text(lastFeeding, color = TextGray, fontSize = 14.sp)
        }

        HorizontalDivider(color = Color(0xFF3D4E63), thickness = 0.5.dp)

        Row {
            Text("Schedule: ", color = TextWhite, fontSize = 14.sp)
            Text(schedule, color = TextGray, fontSize = 14.sp)
        }
    }
}