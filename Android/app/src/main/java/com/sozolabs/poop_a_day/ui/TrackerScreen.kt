package com.sozolabs.poop_a_day.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.res.stringResource
import com.sozolabs.poop_a_day.R
import android.media.MediaPlayer
import com.sozolabs.poop_a_day.ads.AdConfig
import com.sozolabs.poop_a_day.ads.BannerAd
import com.sozolabs.poop_a_day.data.PoopRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

data class Droplet(
    val angle: Float,
    val distance: Float,
    val size: Float
)

@Composable
fun TrackerScreen(
    onPoopLogged: () -> Unit,
    repository: PoopRepository,
    onShowInterstitial: () -> Unit = {},
    isDarkMode: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Animation states
    val poopY = remember { Animatable(-600f) }
    val poopRotation = remember { Animatable(0f) }
    val poopScale = remember { Animatable(1f) }
    val poopOpacity = remember { Animatable(0f) }

    val toiletShakeX = remember { Animatable(0f) }
    val toiletScale = remember { Animatable(1f) }

    val splashProgress = remember { Animatable(0f) }

    val counterScale = remember { Animatable(1f) }

    var isAnimating by remember { mutableStateOf(false) }
    var todayCount by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var droplets by remember { mutableStateOf(emptyList<Droplet>()) }

    // Load today's count and streak
    LaunchedEffect(Unit) {
        val logs = repository.getLogs()
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        todayCount = logs.count { log ->
            val logCal = Calendar.getInstance().apply { timeInMillis = log.timestamp }
            logCal.get(Calendar.DAY_OF_YEAR) == today && logCal.get(Calendar.YEAR) == year
        }
        streak = repository.getStreak()
    }

    // Target Y for toilet center area
    val toiletTargetY = 60f

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Tile background
        TileBackground(isDarkMode = isDarkMode)

        // Top bar: counter
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "💩", fontSize = 28.sp)
                Text(
                    text = "×$todayCount",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF795548),
                    modifier = Modifier.scale(counterScale.value)
                )
            }
            Text(
                text = stringResource(R.string.today),
                fontSize = 14.sp,
                color = Color.Gray
            )
            if (streak > 1) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔥", fontSize = 18.sp)
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                    Text(
                        text = "$streak",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                    Text(
                        text = stringResource(R.string.streak_days),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        // Splash droplets (behind toilet)
        if (splashProgress.value > 0f && splashProgress.value < 1f) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(0f)
            ) {
                val centerX = size.width / 2
                val centerY = size.height / 2 + toiletTargetY - 30f

                for (droplet in droplets) {
                    val x = centerX + cos(droplet.angle) * droplet.distance * splashProgress.value
                    val y = centerY + sin(droplet.angle) * droplet.distance * splashProgress.value
                    val alpha = (1f - splashProgress.value).coerceIn(0f, 1f)
                    val radius = droplet.size * (1f - splashProgress.value * 0.5f)

                    drawCircle(
                        color = Color(0x8042A5F5),
                        radius = radius,
                        center = Offset(x, y),
                        alpha = alpha
                    )
                }
            }
        }

        // Poop emoji
        Text(
            text = "💩",
            fontSize = 80.sp,
            modifier = Modifier
                .offset { IntOffset(0, poopY.value.roundToInt()) }
                .graphicsLayer(
                    rotationZ = poopRotation.value,
                    scaleX = poopScale.value,
                    scaleY = poopScale.value,
                    alpha = poopOpacity.value
                )
                .zIndex(if (poopY.value < toiletTargetY) 2f else 0f)
        )

        // Toilet button
        Text(
            text = "🚽",
            fontSize = 150.sp,
            modifier = Modifier
                .offset { IntOffset(toiletShakeX.value.roundToInt(), toiletTargetY.roundToInt()) }
                .scale(toiletScale.value)
                .zIndex(1f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (!isAnimating) {
                        isAnimating = true

                        // Generate droplets
                        droplets = (0 until 8).map {
                            Droplet(
                                angle = (Math.random() * Math.PI + Math.PI).toFloat(),
                                distance = (30f + Math.random().toFloat() * 50f),
                                size = 4f + Math.random().toFloat() * 6f
                            )
                        }

                        scope.launch {
                            // Reset
                            poopY.snapTo(-600f)
                            poopRotation.snapTo((-15..15).random().toFloat())
                            poopScale.snapTo(1f)
                            poopOpacity.snapTo(1f)
                            splashProgress.snapTo(0f)
                            toiletShakeX.snapTo(0f)
                            toiletScale.snapTo(1f)

                            // Phase 1: Poop falls
                            launch {
                                poopY.animateTo(
                                    targetValue = toiletTargetY - 30f,
                                    animationSpec = tween(durationMillis = 500)
                                )
                            }
                            launch {
                                poopRotation.animateTo(
                                    targetValue = poopRotation.value + (-180..180).random().toFloat(),
                                    animationSpec = tween(durationMillis = 500)
                                )
                            }

                            delay(400)

                            // Phase 2: Shrink into toilet
                            launch {
                                poopScale.animateTo(0.3f, animationSpec = tween(200))
                            }
                            launch {
                                poopOpacity.animateTo(0f, animationSpec = tween(200))
                            }
                            launch {
                                poopY.animateTo(toiletTargetY + 10f, animationSpec = tween(200))
                            }

                            delay(100)

                            // Plop sound
                            try {
                                val mp = MediaPlayer.create(context, R.raw.plop)
                                mp?.setOnCompletionListener { it.release() }
                                mp?.start()
                            } catch (_: Exception) {}

                            // Log it
                            onPoopLogged()
                            todayCount++

                            // Phase 3: Impact effects
                            // Toilet squish
                            launch {
                                toiletScale.animateTo(
                                    1.12f,
                                    animationSpec = spring(
                                        dampingRatio = 0.3f,
                                        stiffness = Spring.StiffnessHigh
                                    )
                                )
                                toiletScale.animateTo(
                                    1f,
                                    animationSpec = spring(
                                        dampingRatio = 0.4f,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }

                            // Toilet shake
                            launch {
                                val shakes = listOf(8f, -8f, 6f, -6f, 4f, -3f, 2f, 0f)
                                for (offset in shakes) {
                                    toiletShakeX.animateTo(
                                        offset,
                                        animationSpec = tween(40)
                                    )
                                }
                            }

                            // Splash
                            launch {
                                splashProgress.animateTo(1f, animationSpec = tween(500))
                            }

                            // Counter pop
                            launch {
                                counterScale.animateTo(
                                    1.3f,
                                    animationSpec = spring(
                                        dampingRatio = 0.4f,
                                        stiffness = Spring.StiffnessHigh
                                    )
                                )
                                counterScale.animateTo(
                                    1f,
                                    animationSpec = spring(
                                        dampingRatio = 0.5f,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }

                            delay(600)
                            isAnimating = false
                            onShowInterstitial()
                        }
                    }
                }
        )

        // Hint text
        if (todayCount == 0 && !isAnimating) {
            Text(
                text = stringResource(R.string.tap_the_toilet),
                fontSize = 16.sp,
                color = Color.Gray.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.offset(y = (toiletTargetY + 100).dp)
            )
        }

        // Banner ad at bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            BannerAd(adUnitId = AdConfig.BANNER_AD_UNIT_ID)
        }
    }
}
