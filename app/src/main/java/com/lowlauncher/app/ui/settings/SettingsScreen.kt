package com.lowlauncher.app.ui.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lowlauncher.app.data.model.AppItem
import com.lowlauncher.app.ui.theme.DarkSurface
import com.lowlauncher.app.ui.theme.DarkSurfaceElevated
import com.lowlauncher.app.ui.theme.PureBlack
import com.lowlauncher.app.ui.theme.PureWhite
import com.lowlauncher.app.ui.theme.SoftWhite
import com.lowlauncher.app.ui.theme.SubtleGray
import com.lowlauncher.app.ui.theme.WhatsAppGreen

@Composable
fun SettingsScreen(
    allApps: List<AppItem>,
    delaySeconds: Int,
    showScreenTime: Boolean,
    hasUsagePermission: Boolean,
    onToggleFavorite: (String) -> Unit,
    onToggleFriction: (String) -> Unit,
    onDelayChange: (Int) -> Unit,
    onToggleScreenTime: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Favoritos da Home", "Pausa Reflexiva", "Ajustes")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(top = 40.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = PureWhite
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Configurações",
                style = MaterialTheme.typography.titleLarge,
                color = PureWhite
            )
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = PureBlack,
            contentColor = PureWhite,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PureWhite
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selectedTab == index) PureWhite else SubtleGray
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> FavoritesConfigTab(allApps, onToggleFavorite)
            1 -> FrictionConfigTab(allApps, delaySeconds, onToggleFriction, onDelayChange)
            2 -> GeneralSettingsTab(
                context = context,
                showScreenTime = showScreenTime,
                hasUsagePermission = hasUsagePermission,
                onToggleScreenTime = onToggleScreenTime
            )
        }
    }
}

@Composable
private fun FavoritesConfigTab(
    allApps: List<AppItem>,
    onToggleFavorite: (String) -> Unit
) {
    val favoritesCount = allApps.count { it.isFavorite }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Selecione os 4 a 6 apps que terão acesso direto na tela inicial. Menos apps = menor distração.",
            style = MaterialTheme.typography.bodyMedium,
            color = SubtleGray
        )
        Text(
            text = "Selecionados: $favoritesCount",
            style = MaterialTheme.typography.labelSmall,
            color = PureWhite,
            modifier = Modifier.padding(top = 6.dp, bottom = 12.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(allApps, key = { it.packageName }) { app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleFavorite(app.packageName) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = app.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (app.isFavorite) PureWhite else SubtleGray,
                        modifier = Modifier.weight(1f)
                    )

                    Checkbox(
                        checked = app.isFavorite,
                        onCheckedChange = { onToggleFavorite(app.packageName) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PureWhite,
                            checkmarkColor = PureBlack,
                            uncheckedColor = SubtleGray
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun FrictionConfigTab(
    allApps: List<AppItem>,
    delaySeconds: Int,
    onToggleFriction: (String) -> Unit,
    onDelayChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Tempo de respiração antes de abrir apps viciantes:",
            style = MaterialTheme.typography.bodyMedium,
            color = SubtleGray
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Slider(
                value = delaySeconds.toFloat(),
                onValueChange = { onDelayChange(it.toInt()) },
                valueRange = 2f..10f,
                steps = 7,
                modifier = Modifier.weight(1f),
                colors = SliderDefaults.colors(
                    thumbColor = PureWhite,
                    activeTrackColor = PureWhite,
                    inactiveTrackColor = DarkSurfaceElevated
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${delaySeconds}s",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Marque os apps que devem ter pausa reflexiva (ex: redes sociais):",
            style = MaterialTheme.typography.bodyMedium,
            color = SubtleGray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(allApps.filter { !it.isWhatsApp }, key = { it.packageName }) { app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleFriction(app.packageName) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = app.label,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (app.hasFrictionDelay) PureWhite else SubtleGray,
                        modifier = Modifier.weight(1f)
                    )

                    Checkbox(
                        checked = app.hasFrictionDelay,
                        onCheckedChange = { onToggleFriction(app.packageName) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PureWhite,
                            checkmarkColor = PureBlack,
                            uncheckedColor = SubtleGray
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun GeneralSettingsTab(
    context: Context,
    showScreenTime: Boolean,
    hasUsagePermission: Boolean,
    onToggleScreenTime: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Exibir tempo de tela na Home",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PureWhite
                )
                Text(
                    text = "Mostra horas/minutos usados hoje abaixo do relógio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SubtleGray
                )
            }
            Switch(
                checked = showScreenTime,
                onCheckedChange = onToggleScreenTime,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PureBlack,
                    checkedTrackColor = PureWhite,
                    uncheckedThumbColor = SubtleGray,
                    uncheckedTrackColor = DarkSurface
                )
            )
        }

        if (!hasUsagePermission) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .clickable {
                        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    }
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "⚠️ Permissão de Acesso a Uso Necessária",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Toque aqui para autorizar o Android a fornecer as estatísticas de tempo de tela de forma 100% privada e local.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubtleGray
                    )
                }
            }
        }
    }
}
