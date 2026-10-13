package com.lowlauncher.app.data.model

import java.time.LocalDate

data class DayNote(
    val dateIso: String, // "YYYY-MM-DD"
    val content: String = "",
    val tasks: List<DayTask> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val hasContent: Boolean
        get() = content.isNotBlank() || tasks.isNotEmpty()

    val localDate: LocalDate
        get() = LocalDate.parse(dateIso)
}

data class DayTask(
    val id: String,
    val title: String,
    val isDone: Boolean = false
)
