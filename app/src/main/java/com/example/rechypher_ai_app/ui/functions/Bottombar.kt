package com.example.rechypher_ai_app.ui.functions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rechypher_ai_app.R

@Composable
fun BottomNavBar(
    selectedItem: Int = 0,
    onItemSelected: (Int) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // White divider to cover the grey line
        HorizontalDivider(
            thickness = 1.dp,
            color = Color.White
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(Color.White)
        ) {
        // Bottom Navigation Items
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Icon  
            NavItemImage(
                imageRes = R.drawable.home,
                label = "Home",
                isSelected = selectedItem == 0,
                onClick = { onItemSelected(0) }
            )

            // Map Icon
            NavItemImage(
                imageRes = R.drawable.location,
                label = "Map",
                isSelected = selectedItem == 1,
                onClick = { onItemSelected(1) }
            )

            // Camera Icon
            NavItemImage(
                imageRes = R.drawable.camera,
                label = "Camera",
                isSelected = selectedItem == 2,
                onClick = { onItemSelected(2) }
            )


            // Chatbot Icon
            NavItemImage(
                imageRes = R.drawable.chatbot,
                label = "Chatbot",
                isSelected = selectedItem == 3,
                onClick = { onItemSelected(3) }
            )
        }
        }
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color(0xFF7BA589) else Color(0xFFB0B0B0),
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color(0xFF7BA589) else Color(0xFFB0B0B0),
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}

@Composable
fun NavItemImage(
    imageRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        modifier = Modifier.width(70.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = imageRes),
                contentDescription = label,
                tint = if (isSelected) Color(0xFF7BA589) else Color(0xFFB0B0B0),
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color(0xFF7BA589) else Color(0xFFB0B0B0),
            fontSize = 10.sp,
            maxLines = 1
        )
    }
}

@Composable
fun PreviewBottomNavBar() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.BottomCenter
        ) {
            BottomNavBar()
        }
    }
}