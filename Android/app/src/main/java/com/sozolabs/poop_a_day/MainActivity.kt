package com.sozolabs.poop_a_day

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sozolabs.poop_a_day.ads.InterstitialAdManager
import com.sozolabs.poop_a_day.data.PoopRepository
import androidx.compose.runtime.LaunchedEffect
import com.sozolabs.poop_a_day.ui.LogScreen
import com.sozolabs.poop_a_day.ui.ProfileScreen
import com.sozolabs.poop_a_day.ui.SplashScreen
import com.sozolabs.poop_a_day.ui.StatsScreen
import com.sozolabs.poop_a_day.ui.TrackerScreen
import kotlinx.coroutines.delay
import com.sozolabs.poop_a_day.ui.theme.PoopADayTheme
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var interstitialAdManager: InterstitialAdManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MobileAds.initialize(this)
        interstitialAdManager = InterstitialAdManager(this)

        val repository = PoopRepository(applicationContext)

        setContent {
            var isDarkMode by rememberSaveable { mutableStateOf(false) }

            var showSplash by rememberSaveable { mutableStateOf(true) }

            PoopADayTheme(darkTheme = isDarkMode) {
                Box(modifier = Modifier.fillMaxSize()) {
                    MainApp(
                        repository = repository,
                        onShowInterstitial = { interstitialAdManager.showAd(this@MainActivity) },
                        isDarkMode = isDarkMode,
                        onToggleTheme = { isDarkMode = !isDarkMode }
                    )

                    AnimatedVisibility(
                        visible = showSplash,
                        exit = fadeOut(animationSpec = tween(400))
                    ) {
                        SplashScreen(isDarkMode = isDarkMode)
                    }
                }

                LaunchedEffect(Unit) {
                    delay(2000)
                    showSplash = false
                }
            }
        }
    }
}

@Composable
fun MainApp(
    repository: PoopRepository,
    onShowInterstitial: () -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                NavigationBarItem(
                    icon = { Text("🚽") },
                    label = { Text(stringResource(R.string.tab_tracker)) },
                    selected = currentRoute == "tracker",
                    onClick = { navController.navigate("tracker") }
                )
                NavigationBarItem(
                    icon = { Text("📝") },
                    label = { Text(stringResource(R.string.tab_log)) },
                    selected = currentRoute == "log",
                    onClick = { navController.navigate("log") }
                )
                NavigationBarItem(
                    icon = { Text("📊") },
                    label = { Text(stringResource(R.string.tab_stats)) },
                    selected = currentRoute == "stats",
                    onClick = { navController.navigate("stats") }
                )
                NavigationBarItem(
                    icon = { Text("👤") },
                    label = { Text(stringResource(R.string.tab_profile)) },
                    selected = currentRoute == "profile",
                    onClick = { navController.navigate("profile") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "tracker",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("tracker") {
                TrackerScreen(
                    onPoopLogged = {
                        scope.launch {
                            repository.addLog()
                        }
                    },
                    repository = repository,
                    onShowInterstitial = onShowInterstitial,
                    isDarkMode = isDarkMode,
                    onToggleTheme = onToggleTheme
                )
            }
            composable("log") {
                LogScreen(repository, isDarkMode)
            }
            composable("stats") {
                StatsScreen(repository, isDarkMode)
            }
            composable("profile") {
                ProfileScreen(repository, isDarkMode, onToggleTheme)
            }
        }
    }
}
