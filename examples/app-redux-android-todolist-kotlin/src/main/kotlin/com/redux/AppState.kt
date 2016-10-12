package com.redux

// state
data class AppState(val list: List<Todo> = listOf<Todo>(),
                    val isFetching: Boolean = false,
                    val editingTodoId: Int? ) : State

// data model
data class Todo(val id: Int, val text: String,
                val isCompleted: Boolean)
