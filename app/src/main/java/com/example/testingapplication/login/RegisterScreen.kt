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
import androidx.compose.material.icons.filled.ArrowBack
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
import com.google.firebase.auth.userProfileChangeRequest

private val BgDark       = Color(0xFF1C2333)
private val CardBg       = Color(0xFF2A3240)
private val AccentBlue   = Color(0xFF3D7BF5)
private val TextWhite    = Color(0xFFFFFFFF)
private val TextGray     = Color(0xFF8A94A6)
private val FieldBg      = Color(0xFF1E2736)
private val BorderColor  = Color(0xFF3A4558)
private val ErrorRed     = Color(0xFFFF5252)
private val SuccessGreen = Color(0xFF4CAF50)

@Composable
fun RegisterScreen(navController: NavController) {

    val auth = remember { FirebaseAuth.getInstance() }

    var fullName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible  by remember { mutableStateOf(false) }
    var termsAccepted   by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }
    var errorMessage    by remember { mutableStateOf("") }
    var successMessage  by remember { mutableStateOf("") }

    val nameError     = fullName.isNotEmpty() && fullName.length < 2
    val emailError    = email.isNotEmpty() && !email.contains("@")
    val passwordError = password.isNotEmpty() && password.length < 6
    val confirmError  = confirmPassword.isNotEmpty() && confirmPassword != password

    fun validateAll(): Boolean {
        return when {
            fullName.isBlank()          -> { errorMessage = "Please enter your full name."; false }
            fullName.length < 2         -> { errorMessage = "Name is too short."; false }
            email.isBlank()             -> { errorMessage = "Please enter your email."; false }
            !email.contains("@")        -> { errorMessage = "Invalid email format."; false }
            password.isBlank()          -> { errorMessage = "Please enter a password."; false }
            password.length < 6         -> { errorMessage = "Password must be at least 6 characters."; false }
            confirmPassword != password -> { errorMessage = "Passwords do not match."; false }
            !termsAccepted              -> { errorMessage = "Please accept the Terms and Conditions."; false }
            else                        -> true
        }
    }

    fun registerWithFirebase() {
        if (!validateAll()) return
        isLoading = true
        errorMessage = ""
        successMessage = ""

        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { result ->
                val profileUpdate = userProfileChangeRequest { displayName = fullName.trim() }
                result.user?.updateProfile(profileUpdate)
                    ?.addOnCompleteListener {
                        isLoading = false
                        successMessage = "Account created! Redirecting..."
                        navController.navigate("dashboard") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
            }
            .addOnFailureListener { exception ->
                isLoading = false
                errorMessage = when {
                    exception.message?.contains("already in use") == true ->
                        "This email is already registered."
                    exception.message?.contains("badly formatted") == true ->
                        "Invalid email format."
                    exception.message?.contains("network") == true ->
                        "Network error. Check your connection."
                    else -> exception.message ?: "Registration failed. Try again."
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(BgDark)
    ) {

        // ── Dog image ──
        Image(
            painter            = painterResource(id = R.drawable.dogo_photo_01),
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .align(Alignment.TopCenter)
        )

        // ── Back button ──
        IconButton(
            onClick  = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 8.dp, start = 8.dp)
        ) {
            Icon(
                imageVector        = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint               = TextWhite
            )
        }

        // ── Scrollable card ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(CardBg)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Avatar inside column so it scrolls ──
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
                text       = "Create Account",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = TextWhite
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text      = "Fill in the details below to get started.",
                fontSize  = 14.sp,
                color     = TextGray,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            // ── Full Name ──
            OutlinedTextField(
                value          = fullName,
                onValueChange  = { fullName = it; errorMessage = "" },
                placeholder    = { Text("Full name", color = TextGray) },
                singleLine     = true,
                isError        = nameError,
                supportingText = if (nameError) {
                    { Text("Name must be at least 2 characters", color = ErrorRed, fontSize = 11.sp) }
                } else null,
                colors   = fieldColors(),
                shape    = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // ── Email ──
            OutlinedTextField(
                value           = email,
                onValueChange   = { email = it; errorMessage = "" },
                placeholder     = { Text("Email address", color = TextGray) },
                singleLine      = true,
                isError         = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                supportingText  = if (emailError) {
                    { Text("Enter a valid email address", color = ErrorRed, fontSize = 11.sp) }
                } else null,
                colors   = fieldColors(),
                shape    = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // ── Password ──
            OutlinedTextField(
                value                = password,
                onValueChange        = { password = it; errorMessage = "" },
                placeholder          = { Text("Password", color = TextGray) },
                singleLine           = true,
                isError              = passwordError,
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector        = if (passwordVisible)
                                Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint               = TextGray
                        )
                    }
                },
                supportingText = if (passwordError) {
                    { Text("Minimum 6 characters", color = ErrorRed, fontSize = 11.sp) }
                } else null,
                colors   = fieldColors(),
                shape    = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            // ── Confirm Password ──
            OutlinedTextField(
                value                = confirmPassword,
                onValueChange        = { confirmPassword = it; errorMessage = "" },
                placeholder          = { Text("Confirm password", color = TextGray) },
                singleLine           = true,
                isError              = confirmError,
                visualTransformation = if (confirmVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        Icon(
                            imageVector        = if (confirmVisible)
                                Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint               = TextGray
                        )
                    }
                },
                supportingText = if (confirmError) {
                    { Text("Passwords do not match", color = ErrorRed, fontSize = 11.sp) }
                } else null,
                colors   = fieldColors(),
                shape    = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // ── Global error / success ──
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
            if (successMessage.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text      = successMessage,
                    color     = SuccessGreen,
                    fontSize  = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Terms checkbox ──
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
                Text("I accept the Terms and Conditions", color = TextGray, fontSize = 14.sp)
            }

            Spacer(Modifier.height(20.dp))

            // ── Sign Up Button ──
            Button(
                onClick  = { registerWithFirebase() },
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
                    Text(
                        "Sign Up",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Already have account ──
            Row {
                Text("Already have an account? ", color = TextGray, fontSize = 14.sp)
                TextButton(
                    onClick        = { navController.popBackStack() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text       = "Log in",
                        color      = AccentBlue,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ── Reusable field colors ──
@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = AccentBlue,
    unfocusedBorderColor    = BorderColor,
    errorBorderColor        = ErrorRed,
    focusedTextColor        = TextWhite,
    unfocusedTextColor      = TextWhite,
    cursorColor             = AccentBlue,
    focusedContainerColor   = FieldBg,
    unfocusedContainerColor = FieldBg,
    errorContainerColor     = FieldBg,
)