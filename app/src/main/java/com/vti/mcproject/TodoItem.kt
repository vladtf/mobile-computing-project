package com.vti.mcproject

/**
 * Data class representing a single todo item.
 */
data class TodoItem(
    val id: Long,
    val text: String,
    var isCompleted: Boolean = false
)
