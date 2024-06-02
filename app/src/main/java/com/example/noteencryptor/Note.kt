package com.example.noteencryptor

data class Note(
    val id: Long = 0,
    val title: String,
    val description: String,
    val timestamp: Long
)