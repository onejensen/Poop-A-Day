package com.sozolabs.poop_a_day.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(isDarkMode: Boolean = false) {
    var titleVisible by remember { mutableStateOf(false) }
    var poopVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        titleVisible = true
        kotlinx.coroutines.delay(300)
        poopVisible = true
    }

    val titleScale by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0.3f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f),
        label = "titleScale"
    )
    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "titleAlpha"
    )
    val poopAlpha by animateFloatAsState(
        targetValue = if (poopVisible) 1f else 0f,
        animationSpec = tween(300),
        label = "poopAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TileBackground(isDarkMode = isDarkMode)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .scale(titleScale)
                    .alpha(titleAlpha)
            ) {
                Text(
                    text = "POOP",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF795548)
                )
                Text(
                    text = "A",
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF795548)
                )
                Text(
                    text = "DAY",
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF795548)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Spinning poop
            Text(
                text = "💩",
                fontSize = 60.sp,
                modifier = Modifier
                    .alpha(poopAlpha)
                    .graphicsLayer(rotationZ = rotation)
            )
        }
    }
}
