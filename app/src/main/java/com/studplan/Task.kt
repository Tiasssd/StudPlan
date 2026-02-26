package com.studplan
/**
 * Модель данных для задачи.
 * @param id уникальный идентификатор задачи
 * @param title краткое название задачи
 * @param description подробное описание (необязательное, но мы добавим)
 * @param day день недели или дата (например, "Понедельник" или "2026-02-24")
 * @param isCompleted выполнена ли задача
 */
data class Task(
    val id: Int,
    val title: String,
    val description: String = "",
    val day: String,
    var isCompleted: Boolean = false
)