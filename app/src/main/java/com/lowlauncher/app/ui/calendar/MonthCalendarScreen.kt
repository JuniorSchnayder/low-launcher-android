package com.lowlauncher.app.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Mail
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lowlauncher.app.data.model.DayNote
import com.lowlauncher.app.ui.theme.BorderGray
import com.lowlauncher.app.ui.theme.DarkSurface
import com.lowlauncher.app.ui.theme.DarkSurfaceElevated
import com.lowlauncher.app.ui.theme.PureBlack
import com.lowlauncher.app.ui.theme.PureWhite
import com.lowlauncher.app.ui.theme.SoftWhite
import com.lowlauncher.app.ui.theme.SubtleGray
import java.time.LocalDate
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

    val monthName = state.currentMonth.month.getDisplayName(TextStyle.FULL, localePt).uppercase()
    val headerTitle = "$monthName ${state.currentMonth.year}"

    val today = LocalDate.now()
    val firstDayOfMonth = state.currentMonth.atDay(1)
    // Sunday = 0, Monday = 1 ... Saturday = 6
    val firstDayOfWeekIndex = firstDayOfMonth.dayOfWeek.value % 7
    val startDate = firstDayOfMonth.minusDays(firstDayOfWeekIndex.toLong())

    // 6 weeks x 7 days = 42 cells total (standard full-screen monthly table)
    val totalDays = 42

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .padding(top = 36.dp, bottom = 24.dp)
    ) {
        // 1. HEADER: Chevron Left | Pill Capsule "OUTUBRO 2026" | Chevron Right
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.goToPreviousMonth() },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Mês anterior",
                    tint = PureWhite,
                    modifier = Modifier.size(36.dp)
                )
            }

            // White capsule with bold black text (as in reference image)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PureWhite)
                    .clickable { viewModel.goToToday() }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = headerTitle,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                    fontWeight = FontWeight.Black,
                    color = PureBlack,
                    letterSpacing = 1.sp
                )
            }

            IconButton(
                onClick = { viewModel.goToNextMonth() },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Próximo mês",
                    tint = PureWhite,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 2. WEEKDAY ROW (DOM | SEG | TER | QUA | QUI | SEX | SÁB)
        val weekDays = listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 0.8.dp, color = BorderGray)
                .background(DarkSurfaceElevated)
                .padding(vertical = 4.dp)
        ) {
            weekDays.forEachIndexed { idx, dayName ->
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    fontWeight = FontWeight.Bold,
                    color = SoftWhite,
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (idx < 6) Modifier.border(width = 0.5.dp, color = BorderGray) else Modifier
                        )
                        .padding(vertical = 2.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        // 3. FULL SCREEN CALENDAR TABLE GRID (6 rows x 7 columns)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(width = 0.8.dp, color = BorderGray)
        ) {
            for (week in 0 until 6) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .then(
                            if (week < 5) Modifier.border(width = 0.5.dp, color = BorderGray) else Modifier
                        )
                ) {
                    for (col in 0 until 7) {
                        val dayIndex = week * 7 + col
                        val cellDate = startDate.plusDays(dayIndex.toLong())
                        val isCurrentMonth = cellDate.month == state.currentMonth.month
                        val isToday = cellDate == today
                        val isSelected = cellDate == state.selectedDate
                        val dateIso = cellDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
                        val note = state.notesMap[dateIso]

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .then(
                                    if (col < 6) Modifier.border(width = 0.5.dp, color = BorderGray) else Modifier
                                )
                                .background(
                                    when {
                                        !isCurrentMonth -> Color(0xFF141414) // Darker grey for out of month
                                        else -> PureBlack
                                    }
                                )
                                .clickable {
                                    viewModel.selectDate(cellDate)
                                    onNavigateToDayDetail()
                                }
                                .padding(2.dp)
                        ) {
                            MonthCellContent(
                                dayNumber = cellDate.dayOfMonth,
                                isCurrentMonth = isCurrentMonth,
                                isToday = isToday,
                                isSelected = isSelected,
                                note = note
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthCellContent(
    dayNumber: Int,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    note: DayNote?
) {
    val numberColor = when {
        !isCurrentMonth -> Color(0xFF555555)
        isToday -> Color(0xFFFF4444) // Distinct red accent like the reference image
        else -> PureWhite
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Row: (Optional Today Enso / Ring) + Day Number in top-right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(1.dp)
                    .then(
                        if (isToday) {
                            Modifier
                                .border(width = 1.5.dp, color = Color(0xFFFF4444), shape = CircleShape)
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        } else {
                            Modifier.padding(horizontal = 2.dp)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayNumber.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = numberColor,
                    textAlign = TextAlign.End
                )
            }
        }

        // Cell Body: Note / Event Block (Matching reference style in our app theme)
        if (note != null && note.hasContent) {
            val previewText = when {
                note.content.isNotBlank() -> note.content.trim().lines().firstOrNull() ?: ""
                note.tasks.isNotEmpty() -> note.tasks.first().title
                else -> ""
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 1.dp, vertical = 1.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF262626)) // Subtle dark contrast card fill
                    .border(width = 0.5.dp, color = Color(0xFF444444), shape = RoundedCornerShape(3.dp))
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Mail,
                        contentDescription = "Nota",
                        tint = PureWhite,
                        modifier = Modifier.size(9.dp)
                    )

                    Spacer(modifier = Modifier.height(1.dp))

                    Text(
                        text = previewText.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 7.5.sp,
                            lineHeight = 9.sp,
                            letterSpacing = (-0.2).sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
