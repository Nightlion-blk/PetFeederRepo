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
import com.google.firebase.auth.FirebaseAuth

private val BgDark      = Color(0xFF1C2333)
private val CardBg      = Color(0xFF2A3240)
private val AccentBlue  = Color(0xFF3D7BF5)
private val TextWhite   = Color(0xFFFFFFFF)
private val TextGray    = Color(0xFF8A94A6)
private val FieldBg     = Color(0xFF1E2736)
private val BorderColor = Color(0xFF3A4558)
private val ErrorRed    = Color(0xFFFF5252)

@Composable
fun LoginScreen(navController: NavController) {

    val auth = remember { FirebaseAuth.getInstance() }

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }
    var errorMessage    by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (auth.currentUser != null) {
            navController.navigate("dashboard") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    fun loginWithFirebase() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill in all fields."
            return
        }
        isLoading = true
        errorMessage = ""

        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener {
                isLoading = false
                navController.navigate("dashboard") {
                    popUpTo("login") { inclusive = true }
                }
            }
            .addOnFailureListener { exception ->
                isLoading = false
                errorMessage = when {
                    exception.message?.contains("no user record") == true ->
                        "No account found with this email."
                    exception.message?.contains("password is invalid") == true ->
                        "Incorrect password. Try again."
                    exception.message?.contains("badly formatted") == true ->
                        "Invalid email format."
                    else -> exception.message ?: "Login failed. Try again."
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(BgDark)
    ) {
        Image(
            painter            = painterResource(id = R.drawable.dogo_photo_01),
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.45f)
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(CardBg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height((-40).dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
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

            Spacer(Modifier.height(16.dp))

            Text(
                text       = "Login account",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = TextWhite
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text       = "Welcome! Please enter your information\nbelow to login.",
                fontSize   = 14.sp,
                color      = TextGray,
                textAlign  = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(28.dp))

            // ── Email Field ──
            OutlinedTextField(
                value           = email,
                onValueChange   = { email = it; errorMessage = "" },
                placeholder     = { Text("Your email", color = TextGray) },
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors          = OutlinedTextFieldDefaults.colors(
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
            Spacer(Modifier.height(12.dp))

            // ── Password Field ──
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it; errorMessage = "" },
                placeholder   = { Text("Password", color = TextGray) },
                singleLine    = true,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon  = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector        = if (passwordVisible)
                                Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint               = TextGray
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

            if (errorMessage.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text      = errorMessage,
                    color     = ErrorRed,
                    fontSize  = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Login Button ──
            Button(
                onClick  = { loginWithFirebase() },
                enabled  = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = AccentBlue,
                    disabledContainerColor = AccentBlue.copy(alpha = 0.4f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color       = TextWhite,
                        modifier    = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick  = { navController.navigate("register") },
                enabled  = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue)
            ) {
                Text("Create account", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}