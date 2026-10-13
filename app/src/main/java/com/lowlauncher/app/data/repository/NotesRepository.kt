package com.lowlauncher.app.data.repository

import android.content.Context
import com.lowlauncher.app.data.model.DayNote
import com.lowlauncher.app.data.model.DayTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class NotesRepository(private val context: Context) {

    private val notesDir: File
        get() = File(context.filesDir, "low_notes").apply { if (!exists()) mkdirs() }

    private val _notesMapFlow = MutableStateFlow<Map<String, DayNote>>(emptyMap())
    val notesMapFlow: Flow<Map<String, DayNote>> = _notesMapFlow.asStateFlow()

    init {
        loadAllNotes()
    }

    private fun loadAllNotes() {
        val files = notesDir.listFiles { file -> file.extension == "json" } ?: emptyArray()
        val map = mutableMapOf<String, DayNote>()
        files.forEach { file ->
            try {
                val jsonString = file.readText()
                val json = JSONObject(jsonString)
                val dateIso = json.optString("dateIso", file.nameWithoutExtension)
                val content = json.optString("content", "")
                val updatedAt = json.optLong("updatedAt", System.currentTimeMillis())

                val tasksList = mutableListOf<DayTask>()
                val tasksArray = json.optJSONArray("tasks")
                if (tasksArray != null) {
                    for (i in 0 until tasksArray.length()) {
                        val taskObj = tasksArray.getJSONObject(i)
                        tasksList.add(
                            DayTask(
                                id = taskObj.optString("id", i.toString()),
                                title = taskObj.optString("title", ""),
                                isDone = taskObj.optBoolean("isDone", false)
                            )
                        )
                    }
                }

                val dayNote = DayNote(
                    dateIso = dateIso,
                    content = content,
                    tasks = tasksList,
                    updatedAt = updatedAt
                )
                if (dayNote.hasContent) {
                    map[dateIso] = dayNote
                }
            } catch (_: Exception) {
                // Ignore malformed files
            }
        }
        _notesMapFlow.value = map
    }

    suspend fun getNote(dateIso: String): DayNote = withContext(Dispatchers.IO) {
        _notesMapFlow.value[dateIso] ?: DayNote(dateIso = dateIso)
    }

    suspend fun saveNote(note: DayNote) = withContext(Dispatchers.IO) {
        val file = File(notesDir, "${note.dateIso}.json")
        if (!note.hasContent) {
            if (file.exists()) {
                file.delete()
            }
            val current = _notesMapFlow.value.toMutableMap()
            current.remove(note.dateIso)
            _notesMapFlow.value = current
            return@withContext
        }

        val json = JSONObject().apply {
            put("dateIso", note.dateIso)
            put("content", note.content)
            put("updatedAt", System.currentTimeMillis())
            val tasksArray = JSONArray()
            note.tasks.forEach { task ->
                tasksArray.put(JSONObject().apply {
                    put("id", task.id)
                    put("title", task.title)
                    put("isDone", task.isDone)
                })
            }
            put("tasks", tasksArray)
        }

        file.writeText(json.toString())
        val current = _notesMapFlow.value.toMutableMap()
        current[note.dateIso] = note
        _notesMapFlow.value = current
    }
}
