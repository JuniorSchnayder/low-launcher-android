package com.lowlauncher.app.ui.friction

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lowlauncher.app.data.model.AppItem
import com.lowlauncher.app.ui.theme.DarkSurface
import com.lowlauncher.app.ui.theme.PureBlack
import com.lowlauncher.app.ui.theme.PureWhite
import com.lowlauncher.app.ui.theme.SubtleGray
import kotlinx.coroutines.delay

@Composable
fun MindfulDelayDialog(
    app: AppItem,
    delaySeconds: Int,
    onDismiss: () -> Unit,
    onProceed: () -> Unit
) {
    var secondsRemaining by remember { mutableIntStateOf(delaySeconds) }
    val progress = (delaySeconds - secondsRemaining).toFloat() / delaySeconds.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DarkSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Pausa Reflexiva",
                    style = MaterialTheme.typography.labelSmall,
                    color = SubtleGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(90.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(90.dp),
                        color = PureWhite,
                        trackColor = PureBlack,
                        strokeWidth = 3.dp
                    )
                    Text(
                        text = if (secondsRemaining > 0) "${secondsRemaining}s" else "✓",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Você realmente precisa abrir o ${app.label} agora?",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = PureWhite
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Respire fundo por alguns segundos antes de decidir.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = SubtleGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Primary detox choice: Desistir e economizar tempo
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PureWhite,
                        contentColor = PureBlack
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Não abrir (voltar ao foco)",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Secondary choice: Proceed after reflection
                OutlinedButton(
                    onClick = onProceed,
                    enabled = secondsRemaining == 0,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = PureWhite,
                        disabledContentColor = SubtleGray
                    )
                ) {
                    Text(
                        text = if (secondsRemaining > 0) "Aguarde o respiro..." else "Abrir mesmo assim"
                    )
                }
            }
        }
    }
}
