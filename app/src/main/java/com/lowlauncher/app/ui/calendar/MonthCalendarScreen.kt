package com.lowlauncher.app.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lowlauncher.app.ui.theme.DarkSurface
import com.lowlauncher.app.ui.theme.DarkSurfaceElevated
import com.lowlauncher.app.ui.theme.PureBlack
import com.lowlauncher.app.ui.theme.PureWhite
import com.lowlauncher.app.ui.theme.SoftWhite
import com.lowlauncher.app.ui.theme.SubtleGray
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthCalendarScreen(
    viewModel: CalendarViewModel,
    onNavigateToDayDetail: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val localePt = Locale("pt", "BR")

    val monthName = state.currentMonth.month.getDisplayName(TextStyle.FULL, localePt).replaceFirstChar { it.uppercase() }
    val yearString = state.currentMonth.year.toString()

    val today = LocalDate.now()
    val firstDayOfMonth = state.currentMonth.atDay(1)
    val daysInMonth = state.currentMonth.lengthOfMonth()

    // Sunday = 0, Monday = 1 ... Saturday = 6
    val firstDayOfWeekIndex = (firstDayOfMonth.dayOfWeek.value % 7) // 0 for Sunday
    val totalGridSlots = firstDayOfWeekIndex + daysInMonth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(horizontal = 24.dp, vertical = 44.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Month Header Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = monthName,
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp, lineHeight = 38.sp),
                        fontWeight = FontWeight.Light,
                        color = PureWhite
                    )
                    Text(
                        text = yearString,
                        style = MaterialTheme.typography.labelSmall,
                        color = SubtleGray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // "Hoje" shortcut chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface)
                            .clickable { viewModel.goToToday() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Hoje",
                            style = MaterialTheme.typography.labelSmall,
                            color = SoftWhite
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.goToPreviousMonth() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Mês anterior",
                            tint = SoftWhite
                        )
                    }

                    IconButton(
                        onClick = { viewModel.goToNextMonth() },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Próximo mês",
                            tint = SoftWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weekday Headers (Dom, Seg, Ter, Qua, Qui, Sex, Sáb)
            val weekDays = listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekDays.forEach { dayName ->
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = SubtleGray,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 7-Column Days Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(totalGridSlots) { index ->
                    if (index < firstDayOfWeekIndex) {
                        // Empty slot before month start
                        Box(modifier = Modifier.aspectRatio(1f))
                    } else {
                        val dayNumber = index - firstDayOfWeekIndex + 1
                        val dayDate = state.currentMonth.atDay(dayNumber)
                        val isSelected = dayDate == state.selectedDate
                        val isToday = dayDate == today
                        val dateIso = dayDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        val hasNote = state.notesMap[dateIso]?.hasContent == true

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    when {
                                        isSelected -> PureWhite
                                        else -> Color.Transparent
                                    }
                                )
                                .then(
                                    if (isToday && !isSelected) {
                                        Modifier.border(1.dp, SubtleGray, RoundedCornerShape(12.dp))
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable {
                                    viewModel.selectDate(dayDate)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isSelected -> PureBlack
                                        isToday -> PureWhite
                                        else -> SoftWhite
                                    }
                                )

                                // Discrete dot indicating notes
                                if (hasNote) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) PureBlack else PureWhite)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Selected Day Summary Card (Tap to go to Tela 3)
        val selectedDateFormatted = state.selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", localePt))
            .replaceFirstChar { it.uppercase() }
        val noteContent = state.currentDayNote.content.ifBlank {
            if (state.currentDayNote.tasks.isNotEmpty()) {
                val done = state.currentDayNote.tasks.count { it.isDone }
                "$done de ${state.currentDayNote.tasks.size} tarefas concluídas"
            } else {
                "Nenhuma anotação neste dia. Toque para adicionar."
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .clickable { onNavigateToDayDetail() }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = selectedDateFormatted,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp),
                        fontWeight = FontWeight.SemiBold,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = noteContent,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubtleGray,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceElevated)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Ver Dia ➜",
                        style = MaterialTheme.typography.labelSmall,
                        color = PureWhite
                    )
                }
            }
        }
    }
}
