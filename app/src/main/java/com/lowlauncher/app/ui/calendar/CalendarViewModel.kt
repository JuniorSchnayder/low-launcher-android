package com.lowlauncher.app.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lowlauncher.app.data.model.DayNote
import com.lowlauncher.app.data.model.DayTask
import com.lowlauncher.app.data.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.UUID

data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val notesMap: Map<String, DayNote> = emptyMap(),
    val currentDayNote: DayNote = DayNote(dateIso = LocalDate.now().toString())
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NotesRepository(application)

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    init {
        repository.notesMapFlow.onEach { map ->
            val currentDateIso = _uiState.value.selectedDate.format(dateFormatter)
            val note = map[currentDateIso] ?: DayNote(dateIso = currentDateIso)
            _uiState.value = _uiState.value.copy(
                notesMap = map,
                currentDayNote = note
            )
        }.launchIn(viewModelScope)
    }

    fun selectDate(date: LocalDate) {
        val dateIso = date.format(dateFormatter)
        val note = _uiState.value.notesMap[dateIso] ?: DayNote(dateIso = dateIso)
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            currentMonth = YearMonth.of(date.year, date.month),
            currentDayNote = note
        )
    }

    fun goToToday() {
        selectDate(LocalDate.now())
    }

    fun goToPreviousMonth() {
        val prev = _uiState.value.currentMonth.minusMonths(1)
        _uiState.value = _uiState.value.copy(currentMonth = prev)
    }

    fun goToNextMonth() {
        val next = _uiState.value.currentMonth.plusMonths(1)
        _uiState.value = _uiState.value.copy(currentMonth = next)
    }

    fun goToPreviousDay() {
        selectDate(_uiState.value.selectedDate.minusDays(1))
    }

    fun goToNextDay() {
        selectDate(_uiState.value.selectedDate.plusDays(1))
    }

    fun updateNoteContent(newContent: String) {
        val current = _uiState.value.currentDayNote.copy(content = newContent)
        _uiState.value = _uiState.value.copy(currentDayNote = current)
        viewModelScope.launch {
            repository.saveNote(current)
        }
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        val currentTasks = _uiState.value.currentDayNote.tasks.toMutableList()
        currentTasks.add(
            DayTask(
                id = UUID.randomUUID().toString(),
                title = title.trim(),
                isDone = false
            )
        )
        val updated = _uiState.value.currentDayNote.copy(tasks = currentTasks)
        _uiState.value = _uiState.value.copy(currentDayNote = updated)
        viewModelScope.launch {
            repository.saveNote(updated)
        }
    }

    fun toggleTask(taskId: String) {
        val currentTasks = _uiState.value.currentDayNote.tasks.map { task ->
            if (task.id == taskId) task.copy(isDone = !task.isDone) else task
        }
        val updated = _uiState.value.currentDayNote.copy(tasks = currentTasks)
        _uiState.value = _uiState.value.copy(currentDayNote = updated)
        viewModelScope.launch {
            repository.saveNote(updated)
        }
    }

    fun removeTask(taskId: String) {
        val currentTasks = _uiState.value.currentDayNote.tasks.filterNot { it.id == taskId }
        val updated = _uiState.value.currentDayNote.copy(tasks = currentTasks)
        _uiState.value = _uiState.value.copy(currentDayNote = updated)
        viewModelScope.launch {
            repository.saveNote(updated)
        }
    }
}
