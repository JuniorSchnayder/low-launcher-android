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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lowlauncher.app.data.model.AppItem
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.uiState.collectAsState()

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
            .background(PureBlack)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -30f && !state.isDrawerOpen && !state.isSettingsOpen) {
                        viewModel.openDrawer()
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

                // Middle Section: Whitelist Favorites & Priority WhatsApp
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. DEDICATED WHATSAPP QUICK SLOT (Top Priority, Zero Friction)
                    state.whatsAppApp?.let { whatsApp ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(DarkSurface)
                                .clickable { viewModel.onAppClicked(whatsApp) }
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
                                text = "WhatsApp",
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

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. FAVORITE APPS (Selected Whitelist in Pure Typography)
                    if (state.favoriteApps.isEmpty()) {
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
                                    ) { viewModel.onAppClicked(app) }
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.openDrawer() }
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
                        onClick = { viewModel.openSettings() },
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

        // Mindful Friction Delay Modal
        state.pendingFrictionApp?.let { app ->
            MindfulDelayDialog(
                app = app,
                delaySeconds = state.frictionDelaySeconds,
                onDismiss = { viewModel.dismissFrictionDialog() },
                onProceed = { viewModel.launchAppDirectly(app) }
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
                onAppClick = { app -> viewModel.onAppClicked(app) },
                onClose = { viewModel.closeDrawer() }
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
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                onToggleFriction = { viewModel.toggleFriction(it) },
                onDelayChange = { viewModel.setFrictionDelay(it) },
                onToggleScreenTime = { viewModel.toggleShowScreenTime(it) },
                onBack = { viewModel.closeSettings() }
            )
        }
    }
}
