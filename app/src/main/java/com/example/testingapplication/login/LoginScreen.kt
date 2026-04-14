package com.example.testingapplication.login

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.navigation.NavController
import com.example.testingapplication.R

// ── Colors from Figma ──
private val BgDark      = Color(0xFF1C2333)
private val CardBg      = Color(0xFF2A3240)
private val AccentBlue  = Color(0xFF3D7BF5)
private val TextWhite   = Color(0xFFFFFFFF)
private val TextGray    = Color(0xFF8A94A6)
private val FieldBg     = Color(0xFF1E2736)
private val BorderColor = Color(0xFF3A4558)

@Composable
fun LoginScreen(navController: NavController) {

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted   by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(BgDark)
    ) {

        // ── Dog image top half ──
        Image(
            painter            = painterResource(id = R.drawable.dogo_photo_01),
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)        // top 45% of screen
                .align(Alignment.TopCenter)
        )

        // ── Gradient fade from image into card ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.TopCenter)
                .offset(y = (LocalDensity_hack * 0.35f).dp)
        )

        // ── Bottom card sheet ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)        // bottom 65%
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(CardBg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(48.dp))  // space for the avatar overlap

            // ── Title ──
            Text(
                text       = "Login account",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = TextWhite
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text      = "Welcome! Please enter your information\nbelow to login.",
                fontSize  = 14.sp,
                color     = TextGray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(28.dp))

            // ── Email Field ──
            OutlinedTextField(
                value         = email,
                onValueChange = { email = it },
                placeholder   = { Text("Your email", color = TextGray) },
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AccentBlue,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor     = TextWhite,
                    unfocusedTextColor   = TextWhite,
                    cursorColor          = AccentBlue,
                    focusedContainerColor   = FieldBg,
                    unfocusedContainerColor = FieldBg,
                ),
                shape    = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // ── Password Field ──
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it },
                placeholder   = { Text("Password", color = TextGray) },
                singleLine    = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon  = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = TextGray
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = AccentBlue,
                    unfocusedBorderColor    = BorderColor,
                    focusedTextColor        = TextWhite,
                    unfocusedTextColor      = TextWhite,
                    cursorColor             = AccentBlue,
                    focusedContainerColor   = FieldBg,
                    unfocusedContainerColor = FieldBg,
                ),
                shape    = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked         = termsAccepted,
                    onCheckedChange = { termsAccepted = it },
                    colors          = CheckboxDefaults.colors(
                        checkedColor   = AccentBlue,
                        uncheckedColor = BorderColor
                    )
                )
                Spacer(Modifier.width(8.dp))
                Text("Accept Terms and Conditions", color = TextGray, fontSize = 14.sp)
            }

            Spacer(Modifier.height(24.dp))

            // ── Create Account Button ──
            Button(
                onClick  = { navController.navigate("dashboard") },
                enabled  = termsAccepted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = AccentBlue,
                    disabledContainerColor = AccentBlue.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    "Create account",
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            Row {
                Text("Already have an account? ", color = TextGray, fontSize = 14.sp)
                Text(
                    text       = "Log in here!",
                    color      = AccentBlue,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopCenter)
                .offset(y = 250.dp)
                .border(2.dp, AccentBlue, CircleShape)
                .clip(CircleShape)
                .background(CardBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Filled.Person,
                contentDescription = null,
                tint               = AccentBlue,
                modifier           = Modifier.size(36.dp)
            )
        }
    }
}

private val LocalDensity_hack  = 812 * 0.35f
private val LocalDensity_hack2 = 812 * 0.42f