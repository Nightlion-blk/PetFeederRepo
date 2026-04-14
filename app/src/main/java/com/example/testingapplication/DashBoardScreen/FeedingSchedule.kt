package com.example.testingapplication.DashBoardScreen

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Calendar

private val BgDark     = Color(0xFF1C2333)
private val CardBg     = Color(0xFF2A3240)
private val AccentBlue = Color(0xFF3D7BF5)
private val TextWhite  = Color(0xFFFFFFFF)
private val TextGray   = Color(0xFF8A94A6)
private val GreenDot   = Color(0xFF4CAF50)
private val ButtonCard = Color(0xFF2E3A4E)

// Data class to hold a scheduled meal
data class ScheduledMeal(
    val name: String,
    val time: String,
    val grams: Int,
    var isEnabled: Boolean = true
)

@Composable
fun FeedingSchedule(navController: NavController) {
    // Shared meal list state
    var meals by remember {
        mutableStateOf(
            listOf(
                ScheduledMeal("Breakfast", "10:00PM", 20),
                ScheduledMeal("Dinner", "08:00PM", 10),
                ScheduledMeal("Lunch", "12:00AM", 10),
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
    ) {
        FeedingScheduleHeader(navController)
        FeedingAddSchedule(onScheduleAdded = { newMeal ->
            meals = meals + newMeal  // ← adds new meal to the list
        })
        ScheduledMealsList(
            meals = meals,
            onToggle = { index, enabled ->
                meals = meals.toMutableList().also { it[index] = it[index].copy(isEnabled = enabled) }
            }
        )
    }
}

@Composable
fun FeedingScheduleHeader(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
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
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Text(
            text = "Feeding Schedule",
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun FeedingAddSchedule(onScheduleAdded: (ScheduledMeal) -> Unit) {
    var selectedTime by remember { mutableStateOf("") }
    var selectedGrams by remember { mutableStateOf(100) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Add Schedule",
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Time + Grams row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CardBg)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Time picker dropdown
            TimePickerSchedule(
                modifier = Modifier.weight(1f),
                onTimeSelected = { time -> selectedTime = time }
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Grams dropdown
            GramsDropdown(
                modifier = Modifier.weight(1f),
                selectedGrams = selectedGrams,
                onGramsSelected = { selectedGrams = it }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add Feed Schedule button
        Button(
            onClick = {
                if (selectedTime.isNotEmpty()) {
                    onScheduleAdded(
                        ScheduledMeal(
                            name = "Meal",
                            time = selectedTime,
                            grams = selectedGrams
                        )
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonCard)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add",
                tint = AccentBlue
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Add Feed Schedule", color = TextWhite)
        }
    }
}

@Composable
fun GramsDropdown(
    modifier: Modifier = Modifier,
    selectedGrams: Int,
    onGramsSelected: (Int) -> Unit
) {
    val gramOptions = (10..200 step 10).toList()
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "$selectedGrams grams", color = TextWhite, fontSize = 13.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = TextWhite
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(CardBg)
        ) {
            gramOptions.forEach { gram ->
                DropdownMenuItem(
                    text = { Text("$gram grams", color = TextWhite) },
                    onClick = {
                        onGramsSelected(gram)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun TimePickerSchedule(
    modifier: Modifier = Modifier,
    onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedTime by remember { mutableStateOf("08:00AM") }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val amPm = if (hour < 12) "AM" else "PM"
            val formattedHour = if (hour % 12 == 0) 12 else hour % 12
            val formattedMinute = String.format("%02d", minute)
            selectedTime = "$formattedHour:$formattedMinute$amPm"
            onTimeSelected(selectedTime)
        },
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().get(Calendar.MINUTE),
        false
    )

    OutlinedButton(
        onClick = { timePickerDialog.show() },
        modifier = modifier,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = selectedTime, color = TextWhite, fontSize = 13.sp)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = "Pick Time",
            tint = TextWhite
        )
    }
}

// Scheduled Meals Section
@Composable
fun ScheduledMealsList(
    meals: List<ScheduledMeal>,
    onToggle: (Int, Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Scheduled Meals",
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        meals.forEachIndexed { index, meal ->
            MealCard(
                meal = meal,
                onToggle = { enabled -> onToggle(index, enabled) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MealCard(meal: ScheduledMeal, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = meal.name,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = " everyday",
                    color = TextGray,
                    fontSize = 12.sp
                )
                Text(text = "  |  ", color = TextGray, fontSize = 12.sp)
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = " ${meal.time} - ${meal.grams}g",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }
        }

        // Toggle switch
        Switch(
            checked = meal.isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = TextWhite,
                checkedTrackColor = AccentBlue
            )
        )
    }
}