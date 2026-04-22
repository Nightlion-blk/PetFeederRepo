package com.example.testingapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.testingapplication.DashBoardScreen.AddDeviceScreen
import com.example.testingapplication.DashBoardScreen.DashboardScreen
import com.example.testingapplication.DashBoardScreen.FeedingSchedule
import com.example.testingapplication.DashBoardScreen.FoodLevel
import com.example.testingapplication.DashBoardScreen.NotificationScreen
import com.example.testingapplication.DashBoardScreen.PetProfile
import com.example.testingapplication.DashBoardScreen.SettingsScreen
import com.example.testingapplication.FeedingHistory.FeedingHistory
import com.example.testingapplication.login.LoginScreen
import com.example.testingapplication.login.RegisterScreen
import com.example.testingapplication.ui.splash.SplashScreen
import com.example.testingapplication.ui.theme.TestingApplicationTheme
import com.google.firebase.database.FirebaseDatabase

const val DB_URL =
    "https://petfeeder-fdce3-default-rtdb.asia-southeast1.firebasedatabase.app"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Using correct Asia database URL
        val database = FirebaseDatabase.getInstance(DB_URL)
        val ref = database.getReference("test")
        ref.setValue("Application: Firebase Connected!")
        Log.d("PetFeeder", "Firebase Connected!")

        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            TestingApplicationTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = "login"
    ) {
        composable("splash") {
            SplashScreen(navController)
        }
        composable("register") {
            RegisterScreen(navController)
        }
        composable("login") {
            LoginScreen(navController)
        }
        composable("dashboard") {
            DashboardScreen(navController)
        }
        composable("history") {
            FeedingHistory(navController)
        }
        composable("schedule") {
            FeedingSchedule(navController)
        }
        composable("notification") {
            NotificationScreen(navController)
        }
        composable("foodLevel") {
            FoodLevel(navController)
        }
        composable("petProfile") {
            PetProfile(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
        composable("addDevice") {
            AddDeviceScreen(navController)
        }
    }
}