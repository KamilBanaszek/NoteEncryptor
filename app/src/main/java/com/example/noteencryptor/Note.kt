package com.example.noteencryptor

data class Note(
    val id: Long = 0,
    var title: String,
    var description: String,
    var timestamp: Long
)