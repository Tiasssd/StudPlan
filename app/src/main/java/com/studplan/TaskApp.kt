@file:OptIn(ExperimentalMaterial3Api::class)

package com.studplan

import android.content.res.Configuration   // <-- Добавь этот импорт для ориентации
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration   // <-- Добавь этот импорт
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.studplan.ui.theme.StudPlanTheme

@Composable
fun TaskApp() {
    val viewModel: TaskViewModel = viewModel()
    val tasks by viewModel.tasks.collectAsState()

    var titleInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var dayInput by remember { mutableStateOf("") }

    // ========== НОВЫЙ КОД: определяем ориентацию ==========
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    // =======================================================

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Планировщик студента") })
        }
    ) { paddingValues ->

        // ========== ИЗМЕНЕНИЯ ЗДЕСЬ ==========
        // Используем Row в альбомной ориентации, Column — в портретной
        if (isLandscape) {
            // Альбомная ориентация: поля слева, список справа
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Левая панель с полями ввода (40% ширины)
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)  // занимает 40% ширины
                        .padding(end = 16.dp)
                ) {
                    InputFields(
                        titleInput = titleInput,
                        onTitleChange = { titleInput = it },
                        descriptionInput = descriptionInput,
                        onDescriptionChange = { descriptionInput = it },
                        dayInput = dayInput,
                        onDayChange = { dayInput = it },
                        onAddClick = {
                            if (titleInput.isNotBlank() && dayInput.isNotBlank()) {
                                viewModel.addTask(titleInput, descriptionInput, dayInput)
                                titleInput = ""
                                descriptionInput = ""
                                dayInput = ""
                            }
                        }
                    )
                }

                // Правая панель со списком задач (оставшиеся 60% ширины)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)  // занимает всё оставшееся место
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onToggleCompleted = { viewModel.toggleCompleted(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        } else {
            // Портретная ориентация: поля сверху, список снизу (как было)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                InputFields(
                    titleInput = titleInput,
                    onTitleChange = { titleInput = it },
                    descriptionInput = descriptionInput,
                    onDescriptionChange = { descriptionInput = it },
                    dayInput = dayInput,
                    onDayChange = { dayInput = it },
                    onAddClick = {
                        if (titleInput.isNotBlank() && dayInput.isNotBlank()) {
                            viewModel.addTask(titleInput, descriptionInput, dayInput)
                            titleInput = ""
                            descriptionInput = ""
                            dayInput = ""
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            onToggleCompleted = { viewModel.toggleCompleted(task) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }
}

// Выносим поля ввода в отдельную функцию, чтобы не дублировать код
@Composable
fun InputFields(
    titleInput: String,
    onTitleChange: (String) -> Unit,
    descriptionInput: String,
    onDescriptionChange: (String) -> Unit,
    dayInput: String,
    onDayChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Column {
        OutlinedTextField(
            value = titleInput,
            onValueChange = onTitleChange,
            label = { Text("Название задачи") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = descriptionInput,
            onValueChange = onDescriptionChange,
            label = { Text("Описание (необязательно)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dayInput,
            onValueChange = onDayChange,
            label = { Text("День (например, Пн или 24.02)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onAddClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Добавить задачу")
        }
    }
}

// Функция TaskItem остается без изменений — мы её не трогаем
@Composable
fun TaskItem(
    task: Task,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit
) {
    // ... код TaskItem (такой же, как был)
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleCompleted() }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = task.title,
                    style = if (task.isCompleted) {
                        MaterialTheme.typography.bodyLarge.copy(
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    } else {
                        MaterialTheme.typography.bodyLarge
                    }
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = task.day,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TaskAppPreview() {
    StudPlanTheme() { TaskApp() }
}