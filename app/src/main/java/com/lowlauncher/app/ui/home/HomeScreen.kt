package com.lowlauncher.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lowlauncher.app.data.model.AppItem
import com.lowlauncher.app.ui.calendar.CalendarViewModel
import com.lowlauncher.app.ui.calendar.DayDetailScreen
import com.lowlauncher.app.ui.calendar.MonthCalendarScreen
import com.lowlauncher.app.ui.drawer.AppDrawerSheet
import com.lowlauncher.app.ui.friction.MindfulDelayDialog
import com.lowlauncher.app.ui.settings.SettingsScreen
import com.lowlauncher.app.ui.theme.DarkSurface
import com.lowlauncher.app.ui.theme.PureBlack
import com.lowlauncher.app.ui.theme.PureWhite
import com.lowlauncher.app.ui.theme.SoftWhite
import com.lowlauncher.app.ui.theme.SubtleGray
import com.lowlauncher.app.ui.theme.WhatsAppGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    calendarViewModel: CalendarViewModel
) {
    val context = LocalContext.current
    val state by homeViewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    // Update time every second
    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR"))
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now).replaceFirstChar { it.uppercase() }
            delay(1000L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (state.enableWallpaper) Color.Transparent else PureBlack)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -30f && !state.isDrawerOpen && !state.isSettingsOpen) {
                        homeViewModel.openDrawer()
                    }
                }
            }
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PureWhite
            )
        } else {
            // Horizontal 3-Screen Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> MainHomeContent(
                        currentTime = currentTime,
                        currentDate = currentDate,
                        state = state,
                        onAppClick = { homeViewModel.onAppClicked(it) },
                        onOpenDrawer = { homeViewModel.openDrawer() },
                        onOpenSettings = { homeViewModel.openSettings() }
                    )
                    1 -> MonthCalendarScreen(
                        viewModel = calendarViewModel,
                        onNavigateToDayDetail = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        }
                    )
                    2 -> DayDetailScreen(
                        viewModel = calendarViewModel,
                        onNavigateBackToMonth = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    )
                }
            }

            // Discreet bottom page indicator (dots: [0: Home, 1: Mês, 2: Dia])
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 6.dp else 4.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) PureWhite else SubtleGray)
                    )
                }
            }
        }

        // Mindful Friction Delay Modal
        state.pendingFrictionApp?.let { app ->
            MindfulDelayDialog(
                app = app,
                delaySeconds = state.frictionDelaySeconds,
                onDismiss = { homeViewModel.dismissFrictionDialog() },
                onProceed = { homeViewModel.launchAppDirectly(app) }
            )
        }

        // App Drawer Overlay
        AnimatedVisibility(
            visible = state.isDrawerOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            AppDrawerSheet(
                allApps = state.allApps,
                onAppClick = { app -> homeViewModel.onAppClicked(app) },
                onClose = { homeViewModel.closeDrawer() }
            )
        }

        // Settings Overlay
        AnimatedVisibility(
            visible = state.isSettingsOpen,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            SettingsScreen(
                allApps = state.allApps,
                delaySeconds = state.frictionDelaySeconds,
                showScreenTime = state.showScreenTime,
                hasUsagePermission = state.usageInfo.hasPermission,
                enableWallpaper = state.enableWallpaper,
                wallpaperDimPercent = state.wallpaperDimPercent,
                onToggleFavorite = { homeViewModel.toggleFavorite(it) },
                onToggleFriction = { homeViewModel.toggleFriction(it) },
                onDelayChange = { homeViewModel.setFrictionDelay(it) },
                onToggleScreenTime = { homeViewModel.toggleShowScreenTime(it) },
                onToggleEnableWallpaper = { homeViewModel.toggleEnableWallpaper(it) },
                onWallpaperDimChange = { homeViewModel.setWallpaperDimPercent(it) },
                onOpenWallpaperPicker = { homeViewModel.openWallpaperPicker(context) },
                onBack = { homeViewModel.closeSettings() }
            )
        }
    }
}

@Composable
private fun MainHomeContent(
    currentTime: String,
    currentDate: String,
    state: HomeUiState,
    onAppClick: (AppItem) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Section: Clock, Date & Screen Time
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = currentTime,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Light,
                color = PureWhite
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = currentDate,
                style = MaterialTheme.typography.bodyMedium,
                color = SubtleGray
            )

            if (state.showScreenTime && state.usageInfo.hasPermission) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "⏳ ${state.usageInfo.formattedDuration}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SubtleGray
                )
            }
        }

        // Middle Section: Whitelist Favorites & Priority WhatsApp(s)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. DEDICATED WHATSAPP QUICK SLOTS (All selected WhatsApps: Normal, Business, Dual)
            if (state.whatsAppApps.isNotEmpty()) {
                state.whatsAppApps.forEach { whatsApp ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (state.enableWallpaper) DarkSurface.copy(alpha = 0.8f) else DarkSurface)
                            .clickable { onAppClick(whatsApp) }
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(WhatsAppGreen)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = whatsApp.label, // shows real label: "WhatsApp" or "WhatsApp Business"
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = PureWhite,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "acesso rápido",
                            style = MaterialTheme.typography.labelSmall,
                            color = SubtleGray
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 2. OTHER FAVORITE APPS (Selected Whitelist in Pure Typography)
            if (state.favoriteApps.isEmpty() && state.whatsAppApps.isEmpty()) {
                Text(
                    text = "Nenhum favorito selecionado.\nToque na engrenagem abaixo para escolher seus 4 a 6 apps essenciais.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubtleGray,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                state.favoriteApps.forEach { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onAppClick(app) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = app.label,
                            style = MaterialTheme.typography.headlineMedium,
                            color = SoftWhite,
                            modifier = Modifier.weight(1f)
                        )

                        if (app.hasFrictionDelay) {
                            Text(
                                text = "⏱ pausa reflexiva",
                                style = MaterialTheme.typography.labelSmall,
                                color = SubtleGray
                            )
                        }
                    }
                }
            }
        }

        // Bottom Section: Search trigger & Settings button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onOpenDrawer() }
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Buscar aplicativos",
                    tint = SubtleGray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "buscar apps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubtleGray
                )
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configurações",
                    tint = SubtleGray
                )
            }
        }
    }
}
