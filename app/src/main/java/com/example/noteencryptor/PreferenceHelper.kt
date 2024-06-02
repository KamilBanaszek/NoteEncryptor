package com.example.noteencryptor

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PreferenceHelper {

    private const val PREFS_NAME = "notes_prefs"
    private const val NOTES_KEY = "notes"

    fun getNotes(context: Context): List<Note> {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val notesJson = sharedPreferences.getString(NOTES_KEY, null) ?: return emptyList()
        val type = object : TypeToken<List<Note>>() {}.type
        return Gson().fromJson(notesJson, type)
    }

    fun saveNotes(context: Context, notes: List<Note>) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val notesJson = Gson().toJson(notes)
        editor.putString(NOTES_KEY, notesJson)
        editor.apply()
    }
}