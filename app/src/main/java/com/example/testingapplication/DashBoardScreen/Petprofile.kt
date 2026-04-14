package com.example.testingapplication.DashBoardScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.testingapplication.R
import kotlin.math.round

@Composable()

fun PetProfile(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        PetProfileHeader(navController)
        Spacer(Modifier.height(16.dp))
        ProfileCard()
        Spacer(Modifier.height(16.dp) )
        AppearanceAndDistinctiveSigns()
        Spacer(modifier = Modifier.height(16.dp))
        ImportantDateCards()
        Spacer(modifier = Modifier.height(16.dp))
        Caretakers()
    }}

@Composable
fun PetProfileHeader(navController: NavController){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 5.dp, bottom = 10.dp)
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
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Text(
            text = "Pet Profile",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ProfileCard(){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .border(3.dp, Color(0xFF4A5060), CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dogo_photo_01),
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)  // optional, for a profile avatar look
                )
            }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                Text(
                        text = "Allen",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Ph | Japan",
                        color = Color(0xFFAAAAAA),
                        fontSize = 13.sp
                    )
            }

        IconButton(onClick = { /* handle click */ }) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        }
    }

@Composable
fun AppearanceAndDistinctiveSigns() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side: label + value stacked vertically
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Appearance & Distinctive Signs",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Light brown with white patches",
                fontWeight = FontWeight.Normal,
                color = Color.White,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gender",
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "Bading",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.Gray.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Size",
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "Medium",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.Gray.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Weight",
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "6kg",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }



        // Right side: label + value stacked vertically, aligned to end

    }

}
@Composable
fun ImportantDateCards() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Section Title
        Text(
            text = "Important Dates",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Birthday Card
        ImportantDateItem(
            icon = Icons.Default.CalendarMonth,
            label = "Birthday",
            date = "3 November 2024",
            extra = "1 y.o"
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color.Gray.copy(alpha = 0.3f)
        )

        // Adoption Day Card
        ImportantDateItem(
            icon = Icons.Default.Home,
            label = "Adoption Day",
            date = "14 February 2026",
            extra = null
        )
    }
}

@Composable
fun ImportantDateItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    date: String,
    extra: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF3A6FF8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Label + Date
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = Color.Gray,
                fontSize = 12.sp
            )
            Text(
                text = date,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }

        // Optional extra text (e.g. "1 y.o")
        if (extra != null) {
            Text(
                text = extra,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun Caretakers() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Caretakers",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.dogo_photo_01),
                contentDescription = "Caretaker",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Joren Buagas",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Text(
                    text = "jorenbuagas@gmail.com",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}