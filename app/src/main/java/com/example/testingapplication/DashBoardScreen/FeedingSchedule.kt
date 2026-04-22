package com.example.testingapplication.DashBoardScreen

import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.testingapplication.DB_URL
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

private val BgDark     = Color(0xFF1C2333)
private val CardBg     = Color(0xFF2A3240)
private val AccentBlue = Color(0xFF3D7BF5)
private val TextWhite  = Color(0xFFFFFFFF)
private val TextGray   = Color(0xFF8A94A6)
private val ButtonCard = Color(0xFF2E3A4E)
private val ErrorRed   = Color(0xFFFF5252)

// ─────────────────────────────────────────────
// DATA
// ─────────────────────────────────────────────

// days: "everyday" or comma-separated e.g. "Mon,Wed,Fri"
data class ScheduledMeal(
    val id:        String  = "",
    val name:      String  = "",
    val time:      String  = "",   // 24hr "HH:MM"
    val grams:     Int     = 0,
    val days:      String  = "everyday",
    var isEnabled: Boolean = true
)

// ─────────────────────────────────────────────
// HELPERS
// ─────────────────────────────────────────────

val ALL_DAYS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

fun daysLabel(days: String): String = when (days) {
    "everyday" -> "Everyday"
    else       -> days.split(",").joinToString(", ")
}

fun to12HrDisplay(time24: String): String {
    return try {
        val parts  = time24.split(":")
        val hour   = parts[0].toInt()
        val minute = parts[1].toInt()
        val amPm   = if (hour < 12) "AM" else "PM"
        val h12    = if (hour % 12 == 0) 12 else hour % 12
        "$h12:${String.format("%02d", minute)} $amPm"
    } catch (e: Exception) { time24 }
}

// ─────────────────────────────────────────────
// SCREEN
// ─────────────────────────────────────────────

@Composable
fun FeedingSchedule(navController: NavController) {
    var meals     by remember { mutableStateOf<List<ScheduledMeal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val ref = remember {
        FirebaseDatabase.getInstance(DB_URL).getReference("petfeeder/schedule")
    }

    LaunchedEffect(Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fetched = mutableListOf<ScheduledMeal>()
                for (child in snapshot.children) {
                    fetched.add(
                        ScheduledMeal(
                            id    = child.key ?: "",
                            name  = child.child("name").getValue(String::class.java)  ?: "Meal",
                            time  = child.child("time").getValue(String::class.java)  ?: "",
                            grams = child.child("grams").getValue(Int::class.java)    ?: 0,
                            days  = child.child("days").getValue(String::class.java)  ?: "everyday"
                        )
                    )
                }
                meals     = fetched
                isLoading = false
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("PetFeeder", "❌ Failed: ${error.message}")
                isLoading = false
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
    ) {
        FeedingScheduleHeader(navController)
        FeedingAddSchedule(onScheduleAdded = { newMeal -> meals = meals + newMeal })

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp),
                color    = AccentBlue
            )
        } else {
            ScheduledMealsList(
                meals    = meals,
                onToggle = { index, enabled ->
                    meals = meals.toMutableList().also { it[index] = it[index].copy(isEnabled = enabled) }
                },
                onEdited = { index, newName, newTime, newGrams, newDays ->
                    val meal = meals[index]
                    if (meal.id.isNotEmpty()) {
                        ref.child(meal.id).updateChildren(
                            mapOf("name" to newName, "time" to newTime,
                                "grams" to newGrams, "days" to newDays)
                        )
                    }
                    meals = meals.toMutableList().also {
                        it[index] = it[index].copy(
                            name = newName, time = newTime,
                            grams = newGrams, days = newDays
                        )
                    }
                },
                onDelete = { index ->
                    val meal = meals[index]
                    if (meal.id.isNotEmpty()) ref.child(meal.id).removeValue()
                    meals = meals.toMutableList().also { it.removeAt(index) }
                }
            )
        }
    }
}

// ─────────────────────────────────────────────
// HEADER
// ─────────────────────────────────────────────

@Composable
fun FeedingScheduleHeader(navController: NavController) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 40.dp)) {
        Box(
            modifier         = Modifier.size(44.dp).clip(CircleShape).align(Alignment.CenterStart),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back",
                    tint = TextWhite, modifier = Modifier.size(22.dp))
            }
        }
        Text(
            text       = "Feeding Schedule",
            fontWeight = FontWeight.Bold,
            color      = TextWhite,
            fontSize   = 16.sp,
            modifier   = Modifier.align(Alignment.Center)
        )
    }
}

// ─────────────────────────────────────────────
// ADD SCHEDULE
// ─────────────────────────────────────────────

@Composable
fun FeedingAddSchedule(onScheduleAdded: (ScheduledMeal) -> Unit) {
    var mealName      by remember { mutableStateOf("") }
    var selectedTime  by remember { mutableStateOf("") }
    var selectedGrams by remember { mutableStateOf(100) }
    var selectedDays  by remember { mutableStateOf("everyday") }

    val ref = FirebaseDatabase.getInstance(DB_URL).getReference("petfeeder/schedule")

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

        Text("Add Schedule", fontWeight = FontWeight.Bold,
            color = TextWhite, fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp))

        // ── Name input ──
        OutlinedTextField(
            value         = mealName,
            onValueChange = { mealName = it },
            placeholder   = { Text("Schedule name (e.g. Breakfast)", color = TextGray) },
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            shape         = RoundedCornerShape(10.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = AccentBlue,
                unfocusedBorderColor = TextGray.copy(alpha = 0.4f),
                focusedTextColor     = TextWhite,
                unfocusedTextColor   = TextWhite,
                cursorColor          = AccentBlue
            )
        )

        // ── Time + Grams ──
        Row(
            modifier              = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)).background(CardBg).padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TimePickerSchedule(
                modifier       = Modifier.weight(1f),
                onTimeSelected = { selectedTime = it }
            )
            Spacer(modifier = Modifier.width(12.dp))
            GramsDropdown(
                modifier        = Modifier.weight(1f),
                selectedGrams   = selectedGrams,
                onGramsSelected = { selectedGrams = it }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ── Day selector ──
        DaySelector(
            selectedDays  = selectedDays,
            onDaysChanged = { selectedDays = it }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (selectedTime.isNotEmpty()) {
                    val name = mealName.ifBlank { "Meal" }
                    val data = mapOf(
                        "name"  to name,
                        "time"  to selectedTime,
                        "grams" to selectedGrams,
                        "days"  to selectedDays
                    )
                    ref.push().setValue(data)
                        .addOnSuccessListener { Log.d("PetFeeder", "✅ Schedule saved") }
                        .addOnFailureListener { Log.e("PetFeeder", "❌ Failed: ${it.message}") }

                    onScheduleAdded(ScheduledMeal(
                        name = name, time = selectedTime,
                        grams = selectedGrams, days = selectedDays
                    ))
                    mealName = ""
                }
            },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            colors   = ButtonDefaults.buttonColors(containerColor = ButtonCard)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add", tint = AccentBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Add Feed Schedule", color = TextWhite)
        }
    }
}

// ─────────────────────────────────────────────
// DAY SELECTOR
// ─────────────────────────────────────────────

@Composable
fun DaySelector(
    selectedDays:  String,
    onDaysChanged: (String) -> Unit
) {
    // "everyday" means all days selected; otherwise parse the comma list
    val isEveryday   = selectedDays == "everyday"
    val activeDaySet = if (isEveryday) ALL_DAYS.toSet()
    else selectedDays.split(",").filter { it.isNotEmpty() }.toSet()

    Column(modifier = Modifier.fillMaxWidth()) {

        // ── Everyday toggle row ──
        Row(
            modifier          = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Repeat", color = TextGray, fontSize = 13.sp, modifier = Modifier.weight(1f))

            // "Everyday" chip — acts as a quick select-all / deselect-all toggle
            val everydayActive = isEveryday
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (everydayActive) AccentBlue else CardBg)
                    .border(1.dp, if (everydayActive) AccentBlue else TextGray.copy(0.3f), RoundedCornerShape(8.dp))
                    .clickable {
                        // Toggle: if already everyday → clear all; if not → set everyday
                        onDaysChanged(if (isEveryday) "" else "everyday")
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text       = "Everyday",
                    color      = if (everydayActive) TextWhite else TextGray,
                    fontSize   = 13.sp,
                    fontWeight = if (everydayActive) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }

        // ── Individual day circles (always visible) ──
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ALL_DAYS.forEach { day ->
                val isActive = day in activeDaySet
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(if (isActive) AccentBlue else CardBg)
                        .border(1.dp, if (isActive) AccentBlue else TextGray.copy(0.3f), CircleShape)
                        .clickable {
                            val newSet = activeDaySet.toMutableSet()
                            if (isActive) newSet.remove(day) else newSet.add(day)

                            val newDays = when {
                                // if all 7 selected → snap to "everyday"
                                newSet.containsAll(ALL_DAYS.toSet()) -> "everyday"
                                newSet.isEmpty()                     -> ""   // none = optional/no repeat
                                else -> ALL_DAYS.filter { it in newSet }.joinToString(",")
                            }
                            onDaysChanged(newDays)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = day.first().toString(),
                        color      = if (isActive) TextWhite else TextGray,
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ── Helper hint text ──
        Text(
            text     = when {
                isEveryday            -> "Repeats every day"
                activeDaySet.isEmpty() -> "One-time feed (no repeat)"
                else                  -> "Repeats on: ${daysLabel(selectedDays)}"
            },
            color    = TextGray,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

// ─────────────────────────────────────────────
// GRAMS DROPDOWN
// ─────────────────────────────────────────────

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
            onClick  = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(8.dp)
        ) {
            Text(text = "$selectedGrams grams", color = TextWhite, fontSize = 13.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Dropdown", tint = TextWhite)
        }
        DropdownMenu(
            expanded         = expanded,
            onDismissRequest = { expanded = false },
            modifier         = Modifier.background(CardBg)
        ) {
            gramOptions.forEach { gram ->
                DropdownMenuItem(
                    text    = { Text("$gram grams", color = TextWhite) },
                    onClick = { onGramsSelected(gram); expanded = false }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// TIME PICKER
// ─────────────────────────────────────────────

@Composable
fun TimePickerSchedule(
    modifier: Modifier = Modifier,
    initialTime: String = "08:00 AM",
    onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var displayTime by remember { mutableStateOf(initialTime) }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val amPm         = if (hour < 12) "AM" else "PM"
            val displayHour  = if (hour % 12 == 0) 12 else hour % 12
            val formattedMin = String.format("%02d", minute)
            displayTime = "$displayHour:$formattedMin $amPm"

            val saveTime = String.format("%02d:%02d", hour, minute)
            onTimeSelected(saveTime)
        },
        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
        Calendar.getInstance().get(Calendar.MINUTE),
        false
    )

    OutlinedButton(
        onClick  = { timePickerDialog.show() },
        modifier = modifier,
        shape    = RoundedCornerShape(8.dp)
    ) {
        Text(text = displayTime, color = TextWhite, fontSize = 13.sp)
        Spacer(modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Pick Time", tint = TextWhite)
    }
}

// ─────────────────────────────────────────────
// MEALS LIST
// ─────────────────────────────────────────────

@Composable
fun ScheduledMealsList(
    meals:    List<ScheduledMeal>,
    onToggle: (Int, Boolean) -> Unit,
    onEdited: (index: Int, name: String, time: String, grams: Int, days: String) -> Unit,
    onDelete: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Scheduled Meals", fontWeight = FontWeight.Bold,
            color = TextWhite, fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 12.dp))

        meals.forEachIndexed { index, meal ->
            MealCard(
                meal     = meal,
                onToggle = { enabled -> onToggle(index, enabled) },
                onEdited = { name, time, grams, days -> onEdited(index, name, time, grams, days) },
                onDelete = { onDelete(index) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ─────────────────────────────────────────────
// MEAL CARD
// ─────────────────────────────────────────────

@Composable
fun MealCard(
    meal:     ScheduledMeal,
    onToggle: (Boolean) -> Unit,
    onEdited: (name: String, time: String, grams: Int, days: String) -> Unit,
    onDelete: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editedName     by remember { mutableStateOf(meal.name) }
    var editedTime     by remember { mutableStateOf(meal.time) }
    var editedGrams    by remember { mutableStateOf(meal.grams) }
    var editedDays     by remember { mutableStateOf(meal.days) }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor   = CardBg,
            title = { Text("Edit Schedule", color = TextWhite, fontWeight = FontWeight.Bold) },
            text  = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Name", color = TextGray, fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                    OutlinedTextField(
                        value         = editedName,
                        onValueChange = { editedName = it },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape         = RoundedCornerShape(10.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = AccentBlue,
                            unfocusedBorderColor = TextGray.copy(alpha = 0.4f),
                            focusedTextColor     = TextWhite,
                            unfocusedTextColor   = TextWhite,
                            cursorColor          = AccentBlue
                        )
                    )

                    Text("Time", color = TextGray, fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                    TimePickerSchedule(
                        modifier       = Modifier.fillMaxWidth(),
                        initialTime    = to12HrDisplay(editedTime),
                        onTimeSelected = { editedTime = it }
                    )

                    Spacer(Modifier.height(12.dp))

                    Text("Amount", color = TextGray, fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                    GramsDropdown(
                        modifier        = Modifier.fillMaxWidth(),
                        selectedGrams   = editedGrams,
                        onGramsSelected = { editedGrams = it }
                    )

                    Spacer(Modifier.height(12.dp))

                    Text("Days", color = TextGray, fontSize = 13.sp,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp))
                    DaySelector(
                        selectedDays  = editedDays,
                        onDaysChanged = { editedDays = it }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onEdited(editedName.ifBlank { "Meal" }, editedTime, editedGrams, editedDays)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
                ) { Text("Save", color = TextWhite) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(meal.name, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.CalendarToday, contentDescription = null,
                    tint = TextGray, modifier = Modifier.size(13.dp))
                // Show "One-time" if no days selected
                Text(
                    text  = " ${if (meal.days.isEmpty()) "One-time" else daysLabel(meal.days)}",
                    color = TextGray, fontSize = 12.sp
                )
                Text("  |  ", color = TextGray, fontSize = 12.sp)
                Icon(Icons.Filled.AccessTime, contentDescription = null,
                    tint = TextGray, modifier = Modifier.size(13.dp))
                Text(" ${to12HrDisplay(meal.time)} · ${meal.grams}g",
                    color = TextGray, fontSize = 12.sp)
            }
        }

        IconButton(onClick = { showEditDialog = true }) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit",
                tint = AccentBlue, modifier = Modifier.size(18.dp))
        }

        IconButton(onClick = { onDelete() }) {
            Icon(Icons.Filled.Delete, contentDescription = "Delete",
                tint = ErrorRed, modifier = Modifier.size(18.dp))
        }

        Switch(
            checked         = meal.isEnabled,
            onCheckedChange = onToggle,
            colors          = SwitchDefaults.colors(
                checkedThumbColor = TextWhite,
                checkedTrackColor = AccentBlue
            )
        )
    }
}