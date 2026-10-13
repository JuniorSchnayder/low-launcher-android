package com.lowlauncher.app.ui.calendar

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lowlauncher.app.ui.theme.DarkSurface
import com.lowlauncher.app.ui.theme.DarkSurfaceElevated
import com.lowlauncher.app.ui.theme.PureBlack
import com.lowlauncher.app.ui.theme.PureWhite
import com.lowlauncher.app.ui.theme.SoftWhite
import com.lowlauncher.app.ui.theme.SubtleGray
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DayDetailScreen(
    viewModel: CalendarViewModel,
    onNavigateBackToMonth: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val localePt = Locale("pt", "BR")

    val dayOfWeekName = state.selectedDate.format(DateTimeFormatter.ofPattern("EEEE", localePt))
        .replaceFirstChar { it.uppercase() }
    val fullDateString = state.selectedDate.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", localePt))

    var newTaskText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 24.dp, vertical = 44.dp)
    ) {
        // Top Bar: Back to month button + Previous/Next day navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DarkSurface)
                    .clickable { onNavigateBackToMonth() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "◀ Ver Mês",
                    style = MaterialTheme.typography.labelSmall,
                    color = SoftWhite
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.goToPreviousDay() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Dia anterior",
                        tint = SoftWhite
                    )
                }

                IconButton(
                    onClick = { viewModel.goToNextDay() },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Próximo dia",
                        tint = SoftWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Date Display
        Text(
            text = dayOfWeekName,
            style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp, lineHeight = 38.sp),
            fontWeight = FontWeight.Light,
            color = PureWhite
        )
        Text(
            text = fullDateString,
            style = MaterialTheme.typography.bodyMedium,
            color = SubtleGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Scrollable content: Checklist and Notes
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Checklist do Dia
            item {
                Text(
                    text = "CHECKLIST DO DIA",
                    style = MaterialTheme.typography.labelSmall,
                    color = SubtleGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Add Task Field
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = newTaskText,
                        onValueChange = { newTaskText = it },
                        placeholder = {
                            Text(
                                text = "Nova tarefa do dia...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SubtleGray
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = SoftWhite,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (newTaskText.isNotBlank()) {
                                viewModel.addTask(newTaskText)
                                newTaskText = ""
                            }
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceElevated)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar",
                            tint = PureWhite
                        )
                    }
                }
            }

            // Tasks List
            items(state.currentDayNote.tasks, key = { it.id }) { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurface)
                        .clickable { viewModel.toggleTask(task.id) }
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = task.isDone,
                        onCheckedChange = { viewModel.toggleTask(task.id) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = PureWhite,
                            checkmarkColor = PureBlack,
                            uncheckedColor = SubtleGray
                        )
                    )

                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (task.isDone) SubtleGray else SoftWhite,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { viewModel.removeTask(task.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remover tarefa",
                            tint = SubtleGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Section 2: Low Notes (Anotações Detalhadas com Auto-save)
            item {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "ANOTAÇÕES & PENSAMENTOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = SubtleGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = state.currentDayNote.content,
                    onValueChange = { viewModel.updateNoteContent(it) },
                    placeholder = {
                        Text(
                            text = "Escreva livremente... o que você digitar é salvo automaticamente para este dia.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SubtleGray
                        )
                    },
                    minLines = 6,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = DarkSurface,
                        unfocusedContainerColor = DarkSurface,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = SoftWhite,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
