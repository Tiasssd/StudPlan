package com.studplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {

    // Приватный MutableStateFlow — только ViewModel может изменять список
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    // Публичный StateFlow — UI может только читать и подписываться
    val tasks: StateFlow<List<Task>> = _tasks

    // Счетчик для генерации уникальных ID новых задач
    private var nextId = 1

    /**
     * Добавить новую задачу.
     */
    fun addTask(title: String, description: String, day: String) {
        // Проверяем, что название и день не пустые (можно добавить больше валидации)
        if (title.isBlank() || day.isBlank()) return

        viewModelScope.launch {
            val newTask = Task(
                id = nextId++,
                title = title.trim(),
                description = description.trim(),
                day = day.trim(),
                isCompleted = false
            )
            // Добавляем новую задачу в конец списка
            _tasks.value = _tasks.value + newTask
        }
    }

    /**
     * Удалить задачу.
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.filter { it.id != task.id }
        }
    }

    /**
     * Переключить статус выполнения задачи.
     */
    fun toggleCompleted(task: Task) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.map {
                if (it.id == task.id) {
                    it.copy(isCompleted = !it.isCompleted)   // создаём копию с изменённым статусом
                } else {
                    it
                }
            }
        }
    }
}