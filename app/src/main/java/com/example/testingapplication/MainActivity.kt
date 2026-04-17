package com.example.testingapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.remote.creation.compose.state.log
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
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
import com.example.testingapplication.ui.splash.SplashScreen
import com.example.testingapplication.ui.theme.TestingApplicationTheme
import com.google.firebase.database.BuildConfig
import com.google.firebase.database.FirebaseDatabase


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ No URL needed - google-services.json handles it
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("test")

        ref.setValue("Firebase Connected!")
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
        startDestination = "dashboard"
    ) {
        // 1️⃣ Splash
        composable("splash") {
            SplashScreen(navController)
        }


        composable("login") {
            LoginScreen(navController)
        }

        composable("dashboard") {
            DashboardScreen(navController)
        }

        composable("schedule"){
            FeedingSchedule(navController)
        }

        composable("foodLevel"){
            FoodLevel(navController)
        }

        composable("petProfile"){
            PetProfile(navController)
        }

        composable(route = "notification"){
            NotificationScreen(navController)
        }
        composable(route = "settings"){
            SettingsScreen(navController)
        }
        composable(route = "feedinghistory"){
            FeedingHistory(navController)
        }
        composable("addDevice") {
            AddDeviceScreen(navController)
        }

    }
}