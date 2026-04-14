package com.example.testingapplication.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testingapplication.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    var start by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue   = if (start) 1f else 0f,
        animationSpec = tween(1000)
    )
    val scale by animateFloatAsState(
        targetValue   = if (start) 1f else 0.7f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
    )

    LaunchedEffect(Unit) {
        start = true
        delay(2500)
        navController.navigate("login") {          // ← goes to LOGIN now
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter            = painterResource(id = R.drawable.dogo_photo_01),
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
                .offset(y = 3.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00101113),
                            Color(0xCC101113)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-100).dp)
                .alpha(alpha)
                .scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text       = "PET FEEDER",
                fontSize   = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color      = Color.White,
                letterSpacing = 2.sp
            )
        }
    }
}